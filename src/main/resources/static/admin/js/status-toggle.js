
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
                    // showFlashMessage(data.message, 'success');

                    // Shows message with type and optional custom title
                    showFlashMessage(data.message, 'success', "Success");
                    // Example: showFlashMessage('Status updated', 'success', 'Success');

                } else {
                    showFlashMessage(data.message, 'error', 'Failed');
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
