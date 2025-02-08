document.addEventListener('DOMContentLoaded', () => {
    const ordersTableBody = document.querySelector("#orders-table tbody");
    const orderDetailsModal = new bootstrap.Modal(document.getElementById("orderDetailsModal"));
    const releaseOrderBtn = document.getElementById("release-order-btn");
    const toastElement = document.getElementById("release-order-toast");
    const signoutButton = document.getElementById("signout-button");

    // Function to fetch and display orders
    async function fetchOrders() {
        try {
            const response = await fetch('/api/orders'); // API endpoint to fetch orders
            const orders = await response.json();
            renderOrders(orders);
        } catch (error) {
            console.error('Error fetching orders:', error);
        }
    }

    // Function to render orders in the table
    function renderOrders(orders) {
        ordersTableBody.innerHTML = "";
        orders.forEach(order => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${order.accountName}</td>
                <td>${order.orderId}</td>
                <td>${order.status}</td>
            `;
            row.addEventListener("click", () => showOrderDetails(order));
            ordersTableBody.appendChild(row);
        });
    }

    // Function to show order details in the modal
    function showOrderDetails(order) {
        document.getElementById("order-fullname").textContent = order.accountName;
        document.getElementById("order-id").textContent = order.orderId;
        document.getElementById("order-total").textContent = order.total.toFixed(2);

        const orderItemsList = document.getElementById("order-items");
        orderItemsList.innerHTML = "";
        order.items.forEach(item => {
            const listItem = document.createElement("li");
            listItem.classList.add("list-group-item");
            listItem.textContent = `${item.name} (x${item.quantity}) - $${(item.price * item.quantity).toFixed(2)}`;
            orderItemsList.appendChild(listItem);
        });

        releaseOrderBtn.dataset.orderId = order.orderId;
        orderDetailsModal.show();
    }

    // Function to release an order
    releaseOrderBtn.addEventListener("click", async () => {
        const orderId = releaseOrderBtn.dataset.orderId;

        try {
            await fetch(`/api/orders/${orderId}/release`, { method: "POST" }); // API request to release order

            // Show toast notification
            const toast = new bootstrap.Toast(toastElement);
            toast.show();

            // Close the modal after a short delay
            setTimeout(() => {
                orderDetailsModal.hide();
                fetchOrders(); // Refresh order list
            }, 1500);
        } catch (error) {
            console.error("Error releasing order:", error);
        }
    });

    // Handle Sign Out
    signoutButton.addEventListener("click", () => {
        localStorage.clear();

        // Redirect to admin sign-in page
        window.location.href = "/admin_signin";
    });

    // Fetch orders on page load
    fetchOrders();
});
