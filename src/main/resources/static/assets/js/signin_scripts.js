document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('signin-form').addEventListener('submit', async function (event) {
        event.preventDefault();

        // Retrieve user input
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();

        try {
            const response = await fetch(`/api/user/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (response.ok) {
                const responseData = await response.json();
                localStorage.setItem('userName', responseData.name);
                localStorage.setItem('userEmail', email);

                // Show success toast notification
                const toastElement = document.getElementById('signin-toast-success');
                if (toastElement) {
                    const toast = new bootstrap.Toast(toastElement);
                    toast.show();
                }

                // Redirect to home page after a delay
                setTimeout(() => {
                    window.location.href = '/';
                }, 1500);
            } else {
                // Show error toast for invalid credentials
                const errorToastElement = document.getElementById('signin-toast-error');
                if (errorToastElement) {
                    const errorToast = new bootstrap.Toast(errorToastElement);
                    errorToast.show();
                }
            }
        } catch (error) {
            console.error("Error logging in:", error);
        }
    });
});
