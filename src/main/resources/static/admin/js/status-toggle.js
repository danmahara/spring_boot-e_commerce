// // File: static/admin/js/status-toggle.js

// /**
//  * Initialize status toggle functionality for any module
//  * @param {string} selector - CSS selector for toggle inputs (default: '.toggle-input')
//  * @param {string} baseRoute - Base route for status endpoint (e.g., '/admin/pages', '/admin/users')
//  */
// function initStatusToggle(selector = '.toggle-input', baseRoute = '/admin/pages') {
//     const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
//     const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

//     if (!csrfToken || !csrfHeader) {
//         console.error('CSRF token or header not found');
//         return;
//     }

//     document.querySelectorAll(selector).forEach(toggle => {
//         toggle.addEventListener("change", function () {
//             const itemId = this.getAttribute("data-id");
//             const statusUrl = `${baseRoute}/status?id=${itemId}`;

//             fetch(statusUrl, {
//                 method: "POST",
//                 headers: {
//                     [csrfHeader]: csrfToken,
//                 }
//             })
//                 .then(res => res.json())
//                 .then(data => {
//                     if (data.success) {
//                         showFlashMessage(data.message, 'success');
//                     } else {
//                         showFlashMessage(data.message, 'error');
//                         // Revert toggle state on error
//                         this.checked = !this.checked;
//                     }
//                 })
//                 .catch(error => {
//                     console.error('Error updating status:', error);
//                     showFlashMessage('Failed to update status', 'error');
//                     // Revert toggle state on error
//                     this.checked = !this.checked;
//                 });
//         });
//     });
// }


// // Auto-initialize if data-status-route attribute is present on body/container
// document.addEventListener("DOMContentLoaded", function () {
//     const container = document.querySelector('[data-status-route]');
//     const toggleInputs = document.querySelectorAll('.toggle-input');

//     // Check if toggle inputs exist on the page
//     if (toggleInputs.length > 0) {
//         if (container) {
//             const route = container.getAttribute('data-status-route');
//             const selector = container.getAttribute('data-status-selector') || '.toggle-input';
//             initStatusToggle(selector, route);
//         } else {
//             // If toggles exist but no route is defined, add click handler to show error
//             toggleInputs.forEach(toggle => {
//                 toggle.addEventListener('click', function (e) {
//                     e.preventDefault();
//                     this.checked = !this.checked; // Prevent toggle
//                     showFlashMessage('Route is not defined for Status. Check console for more details.', 'error');
//                     console.error('❌ Status Toggle Error: Missing data-status-route attribute');
//                     console.log('💡 Solution: Add data-status-route="/admin/your-module" to your section fragment div');
//                     console.log('📝 Example: <div th:fragment="section" class="admin-content" data-status-route="/admin/pages">');
//                 });
//             });
//         }
//     }
// });


function initStatusToggleDelegation(containerSelector = '[data-status-route]', selector = '.toggle-input') {
    const container = document.querySelector(containerSelector);
    if (!container) return;

    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

    if (!csrfToken || !csrfHeader) {
        console.error('CSRF token or header not found');
        return;
    }

    container.addEventListener('change', function (e) {
        const toggle = e.target.closest(selector);
        if (!toggle) return; // ignore unrelated inputs

        const itemId = toggle.dataset.id;
        const baseRoute = container.getAttribute('data-status-route') || '/admin/pages';
        const statusUrl = `${baseRoute}/status?id=${itemId}`;

        fetch(statusUrl, {
            method: "POST",
            headers: {
                [csrfHeader]: csrfToken,
            }
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showFlashMessage(data.message, 'success');
                } else {
                    showFlashMessage(data.message, 'error');
                    toggle.checked = !toggle.checked; // revert on error
                }
            })
            .catch(err => {
                console.error('Error updating status:', err);
                showFlashMessage('Failed to update status', 'error');
                toggle.checked = !toggle.checked; // revert on error
            });
    });
}

// Auto-init delegation
document.addEventListener("DOMContentLoaded", function () {
    initStatusToggleDelegation();
});
