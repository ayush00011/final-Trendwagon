document.addEventListener('DOMContentLoaded', () => {
    // Check if user is logged in
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    
    if (!userName || !userEmail) {
        // Redirect to login page if not logged in
        window.location.href = '/signin';
        return;
    }

    // Fetch user details from localStorage
    const userFirstName = userName.split(' ')[0];
    
    document.getElementById('user-name').textContent = userFirstName;
    document.getElementById('user-full-name').textContent = userName;
    document.getElementById('user-email').textContent = userEmail;

    // Handle Sign Out button click
    const signoutButton = document.getElementById('signout-button');
    if (signoutButton) {
        signoutButton.addEventListener('click', () => {
            // Clear localStorage
            localStorage.removeItem('userName');
            localStorage.removeItem('userEmail');

            // Show the toast notification
            const toastElement = document.getElementById('signout-toast');
            if (toastElement) {
                const toast = new bootstrap.Toast(toastElement);
                toast.show();
            }

            // Redirect to home page after a delay
            setTimeout(() => {
                window.location.href = '/';
            }, 2000); // 2-second delay
        });
    }
});