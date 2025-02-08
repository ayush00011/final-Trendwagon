document.addEventListener('DOMContentLoaded', async () => {
    async function checkAuthStatus() {
        try {
            const response = await fetch('/api/auth/status', {
                method: 'GET',
                credentials: 'include'
            });
            return response.ok;
        } catch (error) {
            console.error('Error checking auth status:', error);
            return false;
        }
    }

    const isAuthenticated = await checkAuthStatus();
    if (!isAuthenticated) {
        window.location.href = '/admin_signin';
        return;
    }

    const productGrid = document.getElementById('product-grid');
    const addItemForm = document.getElementById('add-product-form');
    const signoutButton = document.getElementById('signout-button');
    const changePasswordForm = document.getElementById('change-password-form');
    const accountDropdown = document.getElementById('accountDropdown');

    const adminName = localStorage.getItem('adminName') || 'Admin';
    accountDropdown.innerHTML = `<i class="bi bi-person-circle me-2"></i> ${adminName.split(' ')[0]}`;

    async function fetchProducts() {
        try {
            const response = await fetch('/api/products', {
                method: 'GET',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' }
            });

            if (response.status === 401 || response.status === 403) {
                console.error("Unauthorized! Redirecting to login...");
                window.location.href = "/admin_signin";
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const text = await response.text();
            if (!text.trim()) {
                console.warn("Received empty response from the server.");
                renderProducts([]);
                return;
            }

            const products = JSON.parse(text);
            renderProducts(products);
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    }

	function renderProducts(products) {
	    productGrid.innerHTML = ''; // Clear previous products

	    if (products.length === 0) {
	        productGrid.innerHTML = `
	            <div class="text-center col-12 mt-4">
	                <h5 class="text">No products available.</h5>
	                <p class="text">Click "Add Item" to add new products.</p>
	            </div>
	        `;
	        return;
	    }

	    const row = document.createElement('div');
	    row.classList.add('row', 'gx-4', 'gy-4'); // Bootstrap Grid spacing

	    products.forEach(product => {
	        const productCard = document.createElement('div');
	        productCard.classList.add('col-md-4', 'mb-4');

	        productCard.innerHTML = `
	            <div class="card product-card shadow-sm d-flex flex-column">
	                <img src="${product.imagePath}" class="card-img-top product-img" alt="${product.name}">
	                <div class="card-body d-flex flex-column">
	                    <h5 class="card-title">${product.name}</h5>
	                    <p class="card-text">${product.description}</p>
	                    <p class="card-text"><strong>Price:</strong> ₹${product.price}</p>
	                    <p class="card-text"><strong>Quantity:</strong> ${product.quantity}</p>
	                    
	                    <!-- Button Container -->
	                    <div class="d-flex justify-content-between mt-auto">
	                        <button class="btn btn-warning update-product flex-grow-1 me-2" 
	                            data-id="${product.id}" 
	                            data-name="${product.name}" 
	                            data-description="${product.description}" 
	                            data-price="${product.price}" 
	                            data-quantity="${product.quantity}" 
	                            data-image="${product.imagePath}">
	                            Update
	                        </button>
	                        <button class="btn btn-delete remove-product flex-grow-1" data-id="${product.id}">
	                            Remove
	                        </button>
	                    </div>
	                </div>
	            </div>
	        `;

	        row.appendChild(productCard);
	    });

	    productGrid.appendChild(row);

	    // Attach event handlers
	    document.querySelectorAll('.remove-product').forEach(button => {
	        button.addEventListener('click', async (event) => {
	            const productId = event.target.dataset.id;
	            await removeProduct(productId);
	        });
	    });

	    document.querySelectorAll('.update-product').forEach(button => {
	        button.addEventListener('click', (event) => {
	            openUpdateModal(event.target);
	        });
	    });
	}



	// Handle add product form submission with image upload
		addItemForm.addEventListener('submit', async (event) => {
		    event.preventDefault();

		    const formData = new FormData();
		    formData.append("name", document.getElementById('product-name').value);
		    formData.append("description", document.getElementById('product-description').value);
		    formData.append("price", document.getElementById('product-price').value);
		    formData.append("quantity", document.getElementById('product-quantity').value);
		    formData.append("image", document.getElementById('product-image').files[0]); // Ensure file is selected

		    /*console.log([...formData]); // Debugging: Log form data to check values*/

		    try {
		        const response = await fetch('/api/products', {
		            method: 'POST',
				    credentials: "include",
		            body: formData
		        });

		        if (!response.ok) {
		            throw new Error(`Server responded with ${response.status}`);
		        }

		        fetchProducts();
		        addItemForm.reset();
		        bootstrap.Modal.getInstance(document.getElementById('addItemModal')).hide();
		    } catch (error) {
		        console.error('Error adding product:', error);
		    }
		});

	// Open Update Product Modal
	function openUpdateModal(button) {
	    const productId = button.dataset.id;
	    document.getElementById('update-product-id').value = productId;
	    document.getElementById('update-product-name').value = button.dataset.name;
	    document.getElementById('update-product-description').value = button.dataset.description;
	    document.getElementById('update-product-price').value = button.dataset.price;
	    document.getElementById('update-product-quantity').value = button.dataset.quantity;

	    // Display current image in modal
	    const currentImage = document.getElementById('current-product-image');
	    currentImage.src = button.dataset.image;
	    currentImage.style.display = "block";

	    // Show update modal
	    const updateModal = new bootstrap.Modal(document.getElementById('updateItemModal'));
	    updateModal.show();
	}


	// Handle Update Product Form Submission (including Image Update)
	document.getElementById('update-product-form').addEventListener('submit', async (event) => {
	    event.preventDefault();

	    const productId = document.getElementById('update-product-id').value;
	    const formData = new FormData();
	    
	    formData.append("name", document.getElementById('update-product-name').value);
	    formData.append("description", document.getElementById('update-product-description').value);
	    formData.append("price", parseFloat(document.getElementById('update-product-price').value));
	    formData.append("quantity", parseInt(document.getElementById('update-product-quantity').value));

	    // Check if an image is selected
	    const imageInput = document.getElementById('update-product-image');
	    if (imageInput.files.length > 0) {
	        formData.append("image", imageInput.files[0]); // Append new image if selected
	    }

	    try {
	        const response = await fetch(`/api/products/${productId}`, {
	            method: 'PUT',
	            body: formData,
	            credentials: 'include'
	        });

	        if (!response.ok) {
	            throw new Error(`Server responded with ${response.status}`);
	        }

	        fetchProducts(); // Refresh the product list
	        bootstrap.Modal.getInstance(document.getElementById('updateItemModal')).hide();
	    } catch (error) {
	        console.error('Error updating product:', error);
	    }
	});

    async function removeProduct(productId) {
        try {
            await fetch(`/api/products/${productId}`, { method: 'DELETE', credentials : "include"});
            fetchProducts();
        } catch (error) {
            console.error('Error removing product:', error);
        }
    }
	// Handle password change
	   changePasswordForm.addEventListener('submit', async (event) => {
	       event.preventDefault();
	       const currentPassword = document.getElementById('current-password').value;
	       const newPassword = document.getElementById('new-password').value;

	       try {
	           const response = await fetch('/api/admin/change-password', {
	               method: 'PUT', credentials : "include",
	               headers: { 'Content-Type': 'application/json' },
	               body: JSON.stringify({ oldPassword: currentPassword, newPassword: newPassword })
	           });
	           
	           if (response.ok) {
	               alert('Password changed successfully!');
	           } else {
	               alert('Failed to change password. Please try again.');
	           }
	           
	           changePasswordForm.reset();
	           bootstrap.Modal.getInstance(document.getElementById('changePasswordModal')).hide();
	       } catch (error) {
	           console.error('Error changing password:', error);
	       }
	   });

	   // Handle sign-out
	   signoutButton.addEventListener('click', async () => {
	       try {
	           const response = await fetch('/api/admin/logout', {
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
	                   window.location.href = '/admin_signin';
	               }, 2000);
	           } else {
	               alert('Logout failed. Please try again.');
	           }
	       } catch (error) {
	           console.error('Error logging out:', error);
	       }
	   });

    fetchProducts();
});
