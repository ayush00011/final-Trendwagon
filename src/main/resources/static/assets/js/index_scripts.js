document.addEventListener('DOMContentLoaded', async () => {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartBadge = document.querySelector('.bi-cart-fill ~ span');
    const productContainer = document.getElementById('product-container');
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    
    function updateUserUI() {
        const userDropdown = document.getElementById('user-dropdown');
        const signupButton = document.querySelector('#auth-section .nav-link[href*="signup"]');
        const signinButton = document.querySelector('#auth-section .nav-link[href*="signin"]');
        const userNameElement = document.getElementById('user-name');

        if (userName && userEmail) {
            userNameElement.textContent = userName.split(' ')[0]; // Show first name only
            userDropdown.classList.remove('d-none');
            if (signupButton) signupButton.parentElement.style.display = 'none';
            if (signinButton) signinButton.parentElement.style.display = 'none';
        } 
    }

    async function fetchProducts() {
        try {
            const response = await fetch('/api/products/user-view');
            if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

            const products = await response.json();
            renderProducts(products);
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    }

    function renderProducts(products) {
        productContainer.innerHTML = '';
        if (products.length === 0) {
            document.getElementById('no-results').style.display = 'block';
            return;
        }

        products.forEach(product => {
            const productCard = document.createElement('div');
            productCard.classList.add('col-md-4', 'mb-4');
            productCard.innerHTML = `
                <div class="card product-card">
                    <img src="${product.imagePath}" class="card-img-top" alt="${product.name}">
                    <div class="card-body">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="card-text" data-price="${product.price}">${product.description}</p>
                        <a href="#" class="btn btn-primary">Buy Now</a>
                        <button class="btn btn-secondary add-to-cart" data-name="${product.name}" data-price="${product.price}">Add to Cart</button>
                    </div>
                </div>
            `;
            productContainer.appendChild(productCard);
        });

        attachCartHandlers();
    }

    function attachCartHandlers() {
        document.querySelectorAll('.add-to-cart').forEach(button => {
            button.addEventListener('click', event => {
                event.preventDefault();
                const name = button.dataset.name;
                const price = parseFloat(button.dataset.price);
                const existingProduct = cart.find(item => item.name === name);

                if (existingProduct) {
                    existingProduct.quantity += 1;
                } else {
                    cart.push({ name, price, quantity: 1 });
                }

                localStorage.setItem('cart', JSON.stringify(cart));
                updateCartBadge();
                showToast('add-to-cart-toast');
            });
        });
    }

    function updateCartBadge() {
        const totalItems = cart.reduce((total, item) => total + item.quantity, 0);
        if (cartBadge) {
            cartBadge.textContent = totalItems;
            cartBadge.style.display = totalItems > 0 ? 'inline-block' : 'none';
        }
    }

    function handleSignOut() {
        const signoutButton = document.getElementById('signout-button');
        if (signoutButton) {
			// Handle sign-out
			    signoutButton.addEventListener('click', async () => {
			        try {
			            const response = await fetch('/api/user/logout', {
			                method: 'POST'
			            });
			            if (response.ok) {
			                localStorage.clear();
			                
			                // Show sign-out toast notification
			                const toastElement = document.getElementById('signout-toast');
			                if (toastElement) {
			                    const toast = new bootstrap.Toast(toastElement);
			                    toast.show();
			                }
			                
			                setTimeout(() => {
			                    window.location.href = '/signin';
			                }, 2000);
			            } else {
			                alert('Logout failed. Please try again.');
			            }
			        } catch (error) {
			            console.error('Error logging out:', error);
			        }
			    });

        }
    }

    function handleSearch() {
        const searchForm = document.getElementById('search-form');
        if (searchForm) {
            searchForm.addEventListener('submit', (event) => {
                event.preventDefault();
                const query = document.getElementById('search-input').value.toLowerCase().trim();
                const products = document.querySelectorAll('#product-container .col-md-4');
                let hasResults = false;
                
                products.forEach(product => {
                    const title = product.querySelector('.card-title').textContent.toLowerCase();
                    const description = product.querySelector('.card-text').textContent.toLowerCase();
                    if (title.includes(query) || description.includes(query)) {
                        product.style.display = 'block';
                        hasResults = true;
                    } else {
                        product.style.display = 'none';
                    }
                });

                document.getElementById('no-results').style.display = hasResults ? 'none' : 'block';
            });
        }
    }

    function showToast(id) {
        const toastElement = document.getElementById(id);
        if (toastElement) {
            const toast = new bootstrap.Toast(toastElement);
            toast.show();
        }
    }

    // Initialize everything
    updateUserUI();
    updateCartBadge();
    handleSignOut();
    handleSearch();
    await fetchProducts();
});
