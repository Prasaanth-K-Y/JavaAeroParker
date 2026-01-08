<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Commerce Items</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h1 {
            text-align: center;
            color: white;
            margin-bottom: 30px;
            font-size: 2.5em;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .items-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }

        .item-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .item-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4);
        }

        .item-name {
            font-size: 1.5em;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }

        .item-details {
            margin-bottom: 20px;
        }

        .item-detail {
            display: flex;
            justify-content: space-between;
            margin: 10px 0;
            font-size: 1.1em;
        }

        .detail-label {
            font-weight: 600;
            color: #555;
        }

        .detail-value {
            color: #333;
        }

        .price {
            color: #27ae60;
            font-weight: bold;
            font-size: 1.2em;
        }

        .stock {
            color: #e74c3c;
        }

        .quantity-controls {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
            margin: 20px 0;
        }

        .qty-btn {
            width: 40px;
            height: 40px;
            border: none;
            border-radius: 50%;
            background: #667eea;
            color: white;
            font-size: 1.5em;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .qty-btn:hover {
            background: #5568d3;
            transform: scale(1.1);
        }

        .qty-btn:active {
            transform: scale(0.95);
        }

        .qty-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: scale(1);
        }

        .qty-display {
            font-size: 1.3em;
            font-weight: bold;
            min-width: 60px;
            text-align: center;
            padding: 8px 15px;
            background: #f0f0f0;
            border-radius: 8px;
        }

        .add-to-order-btn {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            font-size: 1.1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .add-to-order-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .add-to-order-btn:active {
            transform: translateY(0);
        }

        .add-to-order-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
        }

        .place-order-section {
            text-align: center;
            margin-top: 40px;
        }

        .place-order-btn {
            padding: 20px 50px;
            border: none;
            border-radius: 50px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            font-size: 1.5em;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 10px 30px rgba(245, 87, 108, 0.4);
            text-transform: uppercase;
            letter-spacing: 2px;
        }

        .place-order-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 40px rgba(245, 87, 108, 0.6);
        }

        .place-order-btn:active {
            transform: translateY(0);
        }

        .place-order-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        .empty-message {
            text-align: center;
            color: white;
            font-size: 1.5em;
            margin-top: 50px;
        }

        /* Alert Modal Styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            animation: fadeIn 0.3s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 30px;
            border-radius: 15px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
            animation: slideDown 0.3s ease;
        }

        @keyframes slideDown {
            from {
                transform: translateY(-50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal-header {
            font-size: 1.8em;
            font-weight: bold;
            margin-bottom: 20px;
            color: #333;
            text-align: center;
        }

        .modal-body {
            font-size: 1.1em;
            color: #555;
            margin-bottom: 25px;
            max-height: 400px;
            overflow-y: auto;
        }

        .order-item {
            padding: 10px;
            margin: 10px 0;
            background: #f8f9fa;
            border-radius: 8px;
            display: flex;
            justify-content: space-between;
        }

        .modal-footer {
            text-align: center;
        }

        .close-btn {
            padding: 12px 40px;
            border: none;
            border-radius: 8px;
            background: #667eea;
            color: white;
            font-size: 1.1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .close-btn:hover {
            background: #5568d3;
            transform: translateY(-2px);
        }

        .badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            background: #e74c3c;
            color: white;
            font-size: 0.9em;
            font-weight: bold;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>E-Commerce Items</h1>

        <c:choose>
            <c:when test="${empty items}">
                <div class="empty-message">
                    <p>No items available at the moment.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="items-grid">
                    <c:forEach var="item" items="${items}">
                        <div class="item-card">
                            <div class="item-name">${item.itemName}</div>
                            <div class="item-details">
                                <div class="item-detail">
                                    <span class="detail-label">Item ID:</span>
                                    <span class="detail-value">${item.itemId}</span>
                                </div>
                                <div class="item-detail">
                                    <span class="detail-label">Price:</span>
                                    <span class="detail-value price">$${item.price}</span>
                                </div>
                                <div class="item-detail">
                                    <span class="detail-label">Available Stock:</span>
                                    <span class="detail-value stock">${item.quantity}</span>
                                </div>
                            </div>

                            <c:choose>
                                <c:when test="${item.quantity > 0}">
                                    <div class="quantity-controls">
                                        <button class="qty-btn" data-item-id="${item.itemId}" onclick="decreaseQty(this)">-</button>
                                        <span class="qty-display" id="qty-${item.itemId}">1</span>
                                        <button class="qty-btn" data-item-id="${item.itemId}" data-max-stock="${item.quantity}" onclick="increaseQty(this)">+</button>
                                    </div>

                                    <button class="add-to-order-btn"
                                            data-item-id="${item.itemId}"
                                            data-item-name="${item.itemName}"
                                            data-item-price="${item.price}"
                                            data-item-stock="${item.quantity}"
                                            onclick="addToOrder(this)">
                                        Add to Order
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <div style="text-align: center; padding: 15px; color: #c62828; font-weight: bold; background: #ffebee; border-radius: 8px; margin-top: 10px;">
                                        OUT OF STOCK
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </div>

                <div class="place-order-section">
                    <button class="place-order-btn" onclick="placeOrder()" id="placeOrderBtn" disabled>
                        Place Order <span class="badge" id="orderCount">0</span>
                    </button>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Alert Modal -->
    <div id="alertModal" class="modal">
        <div class="modal-content">
            <div class="modal-header" id="modalHeader">Order Summary</div>
            <div class="modal-body" id="modalBody"></div>
            <div class="modal-footer">
                <button class="close-btn" onclick="closeModal()">Close</button>
            </div>
        </div>
    </div>

    <script>
        // Store for order items
        let orderItems = {};

        // Decrease quantity
        function decreaseQty(button) {
            const itemId = button.getAttribute('data-item-id');
            const qtyElement = document.getElementById('qty-' + itemId);
            let currentQty = parseInt(qtyElement.textContent);
            if (currentQty > 1) {
                qtyElement.textContent = currentQty - 1;
            }
        }

        // Increase quantity
        function increaseQty(button) {
            const itemId = button.getAttribute('data-item-id');
            const qtyElement = document.getElementById('qty-' + itemId);
            let currentQty = parseInt(qtyElement.textContent);
            // No limit - backend will validate when placing order
            qtyElement.textContent = currentQty + 1;
        }

        // Add item to order
        function addToOrder(button) {
            const itemId = parseInt(button.getAttribute('data-item-id'));
            const itemName = button.getAttribute('data-item-name');
            const price = parseInt(button.getAttribute('data-item-price'));
            const maxStock = parseInt(button.getAttribute('data-item-stock'));

            const qtyElement = document.getElementById('qty-' + itemId);
            const quantity = parseInt(qtyElement.textContent);

            // Add or update item in order (no validation here - backend will handle it)
            if (orderItems[itemId]) {
                orderItems[itemId].quantity += quantity;
            } else {
                orderItems[itemId] = {
                    name: itemName,
                    price: price,
                    quantity: quantity,
                    maxStock: maxStock
                };
            }

            // Reset quantity display to 1
            qtyElement.textContent = '1';

            // Update order count
            updateOrderCount();

            // Show success message
            showAlert('Success', itemName + ' x' + quantity + ' added to cart! Click "Place Order" to complete your purchase.');
        }

        // Update order count badge
        function updateOrderCount() {
            const count = Object.keys(orderItems).length;
            const orderCountBadge = document.getElementById('orderCount');
            const placeOrderBtn = document.getElementById('placeOrderBtn');

            orderCountBadge.textContent = count;

            if (count > 0) {
                placeOrderBtn.disabled = false;
            } else {
                placeOrderBtn.disabled = true;
            }
        }

        // Place order
        async function placeOrder() {
            if (Object.keys(orderItems).length === 0) {
                showAlert('Error', 'Your order is empty. Please add items first.');
                return;
            }

            // Show processing message
            const placeOrderBtn = document.getElementById('placeOrderBtn');
            const originalText = placeOrderBtn.innerHTML;
            placeOrderBtn.innerHTML = 'Processing...';
            placeOrderBtn.disabled = true;

            let orderSummary = '<div style="margin-bottom: 15px; font-weight: bold;">Your Order:</div>';
            let totalAmount = 0;
            let allOrdersSuccessful = true;
            let failedOrders = [];
            let failureDetails = [];

            // Process each order by calling the backend API
            for (let itemId in orderItems) {
                const item = orderItems[itemId];
                console.log('Processing order for item:', item); // Debug log
                const itemTotal = item.price * item.quantity;
                totalAmount += itemTotal;

                try {
                    // Call the backend to place the order and send notification
                    const response = await fetch('/jsp/orders', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            itemId: itemId,
                            quantity: item.quantity
                        })
                    });

                    if (response.ok) {
                        const result = await response.json();
                        console.log('Order succeeded for item:', item.name); // Debug log

                        // Success - build with string concatenation
                        let successHtml = '<div class="order-item" style="background: #e8f5e9;">';
                        successHtml += '<div>';
                        successHtml += '<strong>' + (item.name || 'Unknown Item') + '</strong> ✓<br>';
                        successHtml += '<small>Quantity: ' + item.quantity + ' x $' + item.price + '</small>';
                        successHtml += '</div>';
                        successHtml += '<div style="text-align: right; font-weight: bold; color: #27ae60;">$' + itemTotal + '</div>';
                        successHtml += '</div>';

                        orderSummary += successHtml;
                    } else {
                        const error = await response.json();
                        console.log('Order failed for item:', item.name, 'Error:', error); // Debug log

                        allOrdersSuccessful = false;
                        failedOrders.push(item.name);

                        // Check if error message indicates notification status
                        const isNotificationSent = error.message && error.message.includes('NOTIFICATION SENT');
                        const isNotificationFailed = error.message && error.message.includes('NOTIFICATION FAILED');
                        const hasNotificationStatus = isNotificationSent || isNotificationFailed;

                        console.log('Notification Status - Sent:', isNotificationSent, 'Failed:', isNotificationFailed); // Debug log

                        const notificationMessage = isNotificationSent
                            ? '🔔 Notification sent successfully'
                            : '⚠️ Notification failed';

                        // Track detailed failure information
                        const notificationStatusText = isNotificationSent ? 'notification sent' :'notification failed';
                        failureDetails.push({
                            name: item.name,
                            notificationStatus: notificationStatusText,
                            hasNotification: hasNotificationStatus
                        });

                        // Build the error display with proper string concatenation
                        let errorHtml = '<div class="order-item" style="background: #ffebee;">';
                        errorHtml += '<div>';
                        errorHtml += '<strong>' + (item.name || 'Unknown Item') + '</strong> ✗<br>';
                        errorHtml += '<small style="color: #c62828; font-weight: 600;">' + (error.message || 'Order failed') + '</small>';

                        if (hasNotificationStatus) {
                            const notificationColor = isNotificationSent ? '#ff9800' : '#d32f2f';
                            errorHtml += '<br><small style="color: ' + notificationColor + '; font-weight: 600;">' + notificationMessage + '</small>';
                        }

                        errorHtml += '</div>';
                        errorHtml += '<div style="text-align: right; font-weight: bold; color: #c62828;">Failed</div>';
                        errorHtml += '</div>';

                        orderSummary += errorHtml;
                    }
                } catch (error) {
                    allOrdersSuccessful = false;
                    failedOrders.push(item.name);

                    // Track network error
                    failureDetails.push({
                        name: item.name,
                        notificationStatus: 'network error',
                        hasNotification: false
                    });

                    orderSummary += `
                        <div class="order-item" style="background: #ffebee;">
                            <div>
                                <strong>${item.name}</strong> ✗<br>
                                <small style="color: #c62828;">Network error</small>
                            </div>
                            <div style="text-align: right; font-weight: bold; color: #c62828;">
                                Failed
                            </div>
                        </div>
                    `;
                }
            }

            // Add total and status
            orderSummary += '<div style="margin-top: 20px; padding-top: 15px; border-top: 2px solid #ddd; text-align: right;">';
            orderSummary += '<strong style="font-size: 1.3em;">Total: <span style="color: #27ae60;">$' + totalAmount + '</span></strong>';
            orderSummary += '</div>';

            // Add success or failure message
            if (allOrdersSuccessful) {
                orderSummary += '<div style="margin-top: 20px; padding: 15px; background: #e8f5e9; border-radius: 8px; text-align: center;">';
                orderSummary += '<strong style="color: #27ae60;">✓ All orders placed successfully!</strong><br>';
                orderSummary += '<small>Thank you for your purchase.</small>';
                orderSummary += '</div>';
            } else if (failedOrders.length > 0 && failedOrders.length < Object.keys(orderItems).length) {
                // Partial success - show detailed failure info
                const failureList = failureDetails.map(f =>
                    '<strong>' + f.name + '</strong> (' + f.notificationStatus + ')'
                ).join(', ');

                orderSummary += '<div style="margin-top: 20px; padding: 15px; background: #fff3e0; border-radius: 8px;">';
                orderSummary += '<div style="text-align: center; margin-bottom: 10px;">';
                orderSummary += '<strong style="color: #f57c00;">⚠ Partial success</strong>';
                orderSummary += '</div>';
                orderSummary += '<div style="font-size: 0.9em; color: #666;">';
                orderSummary += 'Failed items: ' + failureList;
                orderSummary += '</div>';
                orderSummary += '</div>';
            } else {
                // All orders failed - show detailed failure info
                const failureList = failureDetails.map(f =>
                    '<div style="margin: 5px 0;"><strong>' + f.name + '</strong> - ' + f.notificationStatus + '</div>'
                ).join('');

                orderSummary += '<div style="margin-top: 20px; padding: 15px; background: #ffebee; border-radius: 8px;">';
                orderSummary += '<div style="text-align: center; margin-bottom: 10px;">';
                orderSummary += '<strong style="color: #c62828;">✗ All orders failed</strong>';
                orderSummary += '</div>';
                orderSummary += '<div style="font-size: 0.9em; color: #666; text-align: left;">';
                orderSummary += failureList;
                orderSummary += '</div>';
                orderSummary += '</div>';
            }

            showAlert('Order Confirmation', orderSummary);

            // Reset button
            placeOrderBtn.innerHTML = originalText;

            // Clear order and reload page to refresh stock quantities
            orderItems = {};
            updateOrderCount();

            // Reset all quantity displays to 1
            document.querySelectorAll('.qty-display').forEach(el => {
                el.textContent = '1';
            });

            // Reload page after 3 seconds to refresh stock quantities
            if (allOrdersSuccessful || (failedOrders.length < Object.keys(orderItems).length)) {
                setTimeout(() => {
                    window.location.reload();
                }, 3000);
            }
        }

        // Show alert modal
        function showAlert(title, message) {
            const modal = document.getElementById('alertModal');
            const modalHeader = document.getElementById('modalHeader');
            const modalBody = document.getElementById('modalBody');

            modalHeader.textContent = title;
            modalBody.innerHTML = message;
            modal.style.display = 'block';
        }

        // Close modal
        function closeModal() {
            const modal = document.getElementById('alertModal');
            modal.style.display = 'none';
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('alertModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
