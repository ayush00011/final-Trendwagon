document.addEventListener('DOMContentLoaded', () => {
    // Attach the event listener to the signup form
    const signupForm = document.getElementById('signup-form');
    if (signupForm) {
        signupForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            // Retrieve user input
            const name = document.getElementById('name').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;

            if (!name || !email || !password) {
                alert("Please fill out all fields.");
                return;
            }

            try {
                const registerResponse = await fetch('/api/user/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ name, email, password })
                });

                if (registerResponse.ok) {
                    // Automatically log in the user after registration
                    const loginResponse = await fetch(`/api/user/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });

                    if (loginResponse.ok) {
                        const loginData = await loginResponse.json();
                        localStorage.setItem('userName', loginData.name);
                        localStorage.setItem('userEmail', email);

                        // Show the toast notification
                        const toastElement = document.getElementById('signup-toast');
                        if (toastElement) {
                            const toast = new bootstrap.Toast(toastElement);
                            toast.show();
                        }

                        // Redirect to home page after 2 seconds
                        setTimeout(() => {
                            window.location.href = '/';
                        }, 2000);
                    } else {
                        alert("Login failed after signup. Please try logging in manually.");
                        window.location.href = '/signin';
                    }
                } else {
                    alert("Signup failed. Please try again.");
                }
            } catch (error) {
                console.error("Error signing up or logging in:", error);
                alert("An error occurred. Please try again later.");
            }
        });
    }
});
