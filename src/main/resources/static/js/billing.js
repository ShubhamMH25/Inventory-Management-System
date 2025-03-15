// WebSocket Utility
function initializeWebSocket(callback) {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('WebSocket connected');
        stompClient.subscribe('/topic/bills', function(message) {
            console.log('Received WebSocket message:', message.body);
            const bill = JSON.parse(message.body);
            callback(bill);
        });
    }, function(error) {
        console.error('WebSocket connection error:', error);
        showToast('Real-time updates unavailable', 'error');
    });
}

// Toast Notification Utility
function showToast(message, type = 'info') {
    const bgColors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#fd7e14',
        info: '#3b7dd8'
    };
    Toastify({
        text: message,
        duration: 3000,
        gravity: "top",
        position: "right",
        backgroundColor: bgColors[type],
        stopOnFocus: true,
    }).showToast();
}

// API Utility Functions
async function fetchData(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

async function postData(url, data) {
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

// Alpine.js Component
document.addEventListener('alpine:init', () => {
    Alpine.data('billingApp', () => ({
        currentStep: 1,
        customers: [],
        products: [],
        filteredProducts: [],
        selectedProducts: [],
        searchQuery: '',
        loading: true,
        selectedCustomerId: '',
        selectedCustomer: null,
        showNewCustomerForm: false,
        newCustomer: {
            name: '',
            age: '',
            phone: '',
            email: '',
            address: ''
        },
        totalPrice: 0,

        init() {
            this.fetchCustomers();
            this.fetchProducts();
            // Initialize showNewCustomerForm based on default dropdown value
            this.showNewCustomerForm = this.selectedCustomerId === '';
            console.log('Initial showNewCustomerForm:', this.showNewCustomerForm);
            initializeWebSocket((bill) => {
                const table = document.getElementById("billingHistoryTable").getElementsByTagName('tbody')[0];
                const row = table.insertRow(0);
                row.classList.add('fade-in');
                row.innerHTML = `
                    <td>${bill.id}</td>
                    <td>${bill.customer.name}</td>
                    <td>$${bill.totalPrice.toFixed(2)}</td>
                    <td>${new Date(bill.generatedAt).toLocaleString()}</td>
                    <td><a href="/billing/invoice/${bill.id}" class="btn btn-primary btn-sm">View Invoice</a></td>
                `;
                showToast(`New bill created! ID: ${bill.id}`, 'success');
            });
        },

        async fetchCustomers() {
            try {
                this.customers = await fetchData('/api/customers');
                console.log('Fetched customers:', this.customers);
            } catch (error) {
                console.error('Error fetching customers:', error);
                showToast('Failed to load customers', 'error');
            }
        },

        async fetchProducts() {
            try {
                console.log('Fetching products...');
                this.products = await fetchData('/api/products');
                this.filteredProducts = [...this.products];
                this.loading = false;
                console.log('Fetched products:', this.products);
            } catch (error) {
                console.error('Error fetching products:', error);
                showToast('Failed to load products', 'error');
                this.loading = false;
            }
        },

        filterProducts() {
            this.filteredProducts = this.products.filter(product =>
                product.name.toLowerCase().includes(this.searchQuery.toLowerCase())
            );
        },

        addToSelected(product) {
            if (!this.selectedProducts.some(p => p.id === product.id)) {
                this.selectedProducts.push({ ...product, selectedQuantity: 1 });
                this.calculateTotal();
            }
        },

        removeSelectedProduct(index) {
            this.selectedProducts.splice(index, 1);
            this.calculateTotal();
        },

        increaseQuantity(index) {
            const product = this.selectedProducts[index];
            if (product.selectedQuantity < product.quantity) {
                product.selectedQuantity++;
                this.calculateTotal();
            } else {
                showToast(`Cannot exceed available stock (${product.quantity})`, 'warning');
            }
        },

        decreaseQuantity(index) {
            if (this.selectedProducts[index].selectedQuantity > 1) {
                this.selectedProducts[index].selectedQuantity--;
                this.calculateTotal();
            }
        },

        calculateTotal() {
            this.totalPrice = this.selectedProducts.reduce((total, item) => {
                return total + (item.price * item.selectedQuantity);
            }, 0);
        },

        selectCustomer() {
            console.log('Selected customer ID:', this.selectedCustomerId);
            this.showNewCustomerForm = this.selectedCustomerId === '';
            console.log('showNewCustomerForm updated to:', this.showNewCustomerForm);
            this.selectedCustomer = this.customers.find(c => c.id === parseInt(this.selectedCustomerId)) || null;
            console.log('Selected customer:', this.selectedCustomer);
        },

        async addNewCustomer() {
            try {
                console.log('Adding new customer:', this.newCustomer);
                const newCustomer = await postData('/api/customers', this.newCustomer);
                console.log('New customer added:', newCustomer);
                this.customers.push(newCustomer);
                this.selectedCustomerId = newCustomer.id.toString(); // Ensure it's a string for comparison
                this.selectedCustomer = newCustomer;
                this.showNewCustomerForm = false;
                showToast('Customer added successfully', 'success');
            } catch (error) {
                console.error('Error adding customer:', error);
                showToast('Failed to add customer: ' + error.message, 'error');
            }
        },

        nextStep() {
            if (this.currentStep === 1 && !this.selectedCustomerId) {
                showToast('Please select or add a customer', 'warning');
                return;
            }
            if (this.currentStep === 2 && this.selectedProducts.length === 0) {
                showToast('Please add at least one product', 'warning');
                return;
            }
            if (this.currentStep < 3) this.currentStep++;
        },

        prevStep() {
            if (this.currentStep > 1) this.currentStep--;
        },

		async createBill() {
		    try {
		        const billData = {
		            customerId: parseInt(this.selectedCustomerId),
		            items: this.selectedProducts.map(item => ({
		                productId: item.id,
		                quantity: item.selectedQuantity
		            }))
		        };
		        console.log('Creating bill with data:', billData);
		        const result = await postData('/billing', billData);
		        console.log('Bill creation response:', result);
		        if (result.success) {
		            showToast('Bill created successfully', 'success');
		            if (result.lowStockAlerts) {
		                result.lowStockAlerts.forEach(alert => showToast(alert, 'warning'));
		            }
		            this.resetForm();
		        } else {
		            showToast('Failed to create bill: ' + (result.error || 'Unknown error'), 'error');
		        }
		    } catch (error) {
		        console.error('Error creating bill:', error);
		        showToast('Failed to create bill: ' + error.message, 'error');
		    }
		},

        resetForm() {
            this.currentStep = 1;
            this.selectedCustomerId = '';
            this.selectedCustomer = null;
            this.showNewCustomerForm = false;
            this.newCustomer = { name: '', age: '', phone: '', email: '', address: '' };
            this.selectedProducts = [];
            this.searchQuery = '';
            this.totalPrice = 0;
            this.filteredProducts = [...this.products];
        }
    }));
});