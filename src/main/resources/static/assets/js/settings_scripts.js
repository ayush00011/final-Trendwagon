document.addEventListener('DOMContentLoaded', () => {

    // Check if user is logged in
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');

    if (!userName || !userEmail) {
        // Redirect to sign-in page if not logged in
        window.location.href = '/signin';
    }
    
    // Populate fields with stored user data
    const nameField = document.getElementById('name');
    const emailField = document.getElementById('email');

    nameField.value = userName || '';
    emailField.value = userEmail || '';

    // Handle form submission
    const settingsForm = document.getElementById('settings-form');
    settingsForm.addEventListener('submit', (event) => {
        event.preventDefault();

        // Update localStorage with new data
        const name = nameField.value.trim();
        const email = emailField.value.trim();
        const password = document.getElementById('password').value.trim();

        if (name && email) {
            localStorage.setItem('userName', name);
            localStorage.setItem('userEmail', email);

            // Show success toast
            const successToastElement = document.getElementById('update-toast');
            if (successToastElement) {
                const successToast = new bootstrap.Toast(successToastElement);
                successToast.show();
            }

            setTimeout(() => {
                window.location.href = '/account';
            }, 2000);
        } else {
            // Show error toast
            const errorToastElement = document.getElementById('error-toast');
            if (errorToastElement) {
                const errorToast = new bootstrap.Toast(errorToastElement);
                errorToast.show();
            }
        }
    });

    // Sign-out button
    const signoutButton = document.getElementById('signout-button');
    if (signoutButton) {
        signoutButton.addEventListener('click', () => {
            localStorage.clear();
            
            // Show sign-out toast
            const signoutToastElement = document.getElementById('signout-toast');
            if (signoutToastElement) {
                const signoutToast = new bootstrap.Toast(signoutToastElement);
                signoutToast.show();
            }

            // Redirect to home page after a delay
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
        });
    }
});