document.addEventListener('DOMContentLoaded', () => {
    const cartContainer = document.getElementById('cart-container');
    const cartTableBody = document.querySelector('#cart-table tbody');
    const totalPriceElement = document.getElementById('total-price');
    const emptyCartMessage = document.getElementById('empty-cart-message');
    const authSection = document.getElementById('auth-section');

    // Load cart items from localStorage
    let cartItems = JSON.parse(localStorage.getItem('cart')) || [];

    // Check if the user is logged in
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');

    if (!userName || !userEmail) {
        authSection.innerHTML = `
            <li class="nav-item">
                <a class="nav-link" href="/signup">
                    <i class="bi bi-person-plus-fill me-2"></i>SignUp
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/signin">
                    <i class="bi bi-box-arrow-in-right me-2"></i>SignIn
                </a>
            </li>
        `;
    } else {
        authSection.innerHTML = `
            <li class="nav-item">
                <button class="nav-link text-danger" id="signout-button">
                    <i class="bi bi-box-arrow-right me-2"></i>Sign Out
                </button>
            </li>
        `;
    }

    function updateCart() {
        cartTableBody.innerHTML = '';
        let totalPrice = 0;

        if (cartItems.length === 0) {
            cartContainer.style.display = 'none';
            emptyCartMessage.style.display = 'block';
            return;
        }

        cartContainer.style.display = 'block';
        emptyCartMessage.style.display = 'none';

        cartItems.forEach((item, index) => {
            const total = (item.price * item.quantity).toFixed(2);
            totalPrice += parseFloat(total);

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${item.name}</td>
                <td>
                    <button class="btn btn-sm btn-outline-secondary decrement-btn" data-index="${index}">
                        <i class="bi bi-dash"></i>
                    </button>
                    <span class="mx-2">${item.quantity}</span>
                    <button class="btn btn-sm btn-outline-secondary increment-btn" data-index="${index}">
                        <i class="bi bi-plus"></i>
                    </button>
                </td>
                <td>₹${item.price.toFixed(2)}</td>
                <td>₹${total}</td>
                <td>
                    <button class="btn btn-danger btn-sm remove-btn" data-index="${index}">
                        <i class="bi bi-trash"></i> Remove
                    </button>
                </td>
            `;
            cartTableBody.appendChild(row);
        });

        totalPriceElement.textContent = `Total: ₹${totalPrice.toFixed(2)}`;
    }

    window.incrementQuantity = (index) => {
        cartItems[index].quantity += 1;
        localStorage.setItem('cart', JSON.stringify(cartItems));
        updateCart();
    };

    window.decrementQuantity = (index) => {
        cartItems[index].quantity -= 1;
        if (cartItems[index].quantity <= 0) {
            cartItems.splice(index, 1);
        }
        localStorage.setItem('cart', JSON.stringify(cartItems));
        updateCart();
    };

    window.removeItem = (index) => {
        cartItems.splice(index, 1);
        localStorage.setItem('cart', JSON.stringify(cartItems));
        updateCart();
    };

    cartTableBody.addEventListener('click', (event) => {
        const target = event.target;
        const index = target.closest('button')?.dataset.index;

        if (target.classList.contains('increment-btn') || target.closest('.increment-btn')) {
            incrementQuantity(index);
        } else if (target.classList.contains('decrement-btn') || target.closest('.decrement-btn')) {
            decrementQuantity(index);
        } else if (target.classList.contains('remove-btn') || target.closest('.remove-btn')) {
            removeItem(index);
        }
    });

    document.getElementById('checkout-button').addEventListener('click', () => {
        alert('Proceeding to checkout...');
        localStorage.removeItem('cart');
        window.location.href = '/checkout';
    });

    document.body.addEventListener('click', (event) => {
        if (event.target && event.target.id === 'signout-button') {
            localStorage.clear();
            const toastElement = document.getElementById('signout-toast');
            const toast = new bootstrap.Toast(toastElement);
            toast.show();
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
        }
    });

    updateCart();
});
