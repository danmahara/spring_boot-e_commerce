// This file is now optional - status updates are handled by ajax-table.js
// Keep this file for any legacy implementations that might use it

function initStatusToggleDelegation(containerSelector = 'body', selector = '.toggle-input') {
    const container = document.querySelector(containerSelector);
    if (!container) return;

    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content || 'X-CSRF-TOKEN';

    if (!csrfToken) {
        console.error('CSRF token not found');
        return;
    }

    container.addEventListener('change', function (e) {
        const toggle = e.target.closest(selector);
        if (!toggle) return;

        // If toggle has a data-status-route, use it; otherwise skip
        // (ajax-table.js will handle it with config.statusRoute)
        const statusRoute = toggle.dataset.statusRoute;
        if (!statusRoute) return;

        const itemId = toggle.dataset.id;
        const statusUrl = statusRoute.replace('{id}', itemId);

        fetch(statusUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ status: toggle.checked })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showFlashMessage(data.message || 'Status updated', 'success', "Success");
                } else {
                    showFlashMessage(data.message || 'Failed to update', 'error', 'Failed');
                    toggle.checked = !toggle.checked;
                }
            })
            .catch(err => {
                console.error('Error updating status:', err);
                showFlashMessage('Failed to update status', 'error', 'Failed');
                toggle.checked = !toggle.checked;
            });
    });
}

// Auto-init for legacy implementations
document.addEventListener("DOMContentLoaded", function () {
    initStatusToggleDelegation();
});