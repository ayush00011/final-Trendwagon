document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('admin-signin-form');

	//LOGIN
    
	if (form) {
        form.addEventListener('submit', async function (event) {
            event.preventDefault();

            // Retrieve admin input
            const email = document.getElementById('admin-email').value.trim();
            const password = document.getElementById('admin-password').value.trim();

            try {
                const response = await fetch(`/api/admin/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    if (data && data.name) {
                        localStorage.setItem('adminName', data.name);
                    }
                    // Show success toast
                    const toastElement = document.getElementById('admin-signin-toast-success');
                    if (toastElement) {
                        const toast = new bootstrap.Toast(toastElement);
                        toast.show();
                    }

                    // Redirect to admin dashboard after a delay
                    setTimeout(() => {
                        window.location.href = "/admin_dashboard";
                    }, 1500);
                } else {
                    // Show error toast
                    const errorToastElement = document.getElementById('admin-signin-toast-error');
                    if (errorToastElement) {
                        const toast = new bootstrap.Toast(errorToastElement);
                        toast.show();
                    }
                }
            } catch (error) {
                console.error("Error logging in:", error);
            }
        });
    }
});
