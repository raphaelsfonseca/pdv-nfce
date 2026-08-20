/* ========================================
   PDV NFCe - Global JavaScript Utilities
   ======================================== */

/**
 * Format a number as Brazilian currency (R$ X.XXX,XX)
 */
function formatCurrency(value) {
    if (value === null || value === undefined) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

/**
 * Format a date as DD/MM/YYYY HH:mm
 */
function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('pt-BR') + ' ' + d.toLocaleTimeString('pt-BR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Format a date as DD/MM/YYYY
 */
function formatDateOnly(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('pt-BR');
}

/**
 * Show a toast notification
 */
function showToast(message, type) {
    type = type || 'success';

    // Create toast container if it doesn't exist
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'fixed top-4 right-4 z-50 space-y-3';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const bgColor = type === 'success' ? 'bg-emerald-50 border-emerald-200 text-emerald-800' :
                    type === 'error' ? 'bg-rose-50 border-rose-200 text-rose-800' :
                    type === 'warning' ? 'bg-amber-50 border-amber-200 text-amber-800' :
                    'bg-blue-50 border-blue-200 text-blue-800';

    const iconColor = type === 'success' ? 'text-emerald-600' :
                      type === 'error' ? 'text-rose-600' :
                      type === 'warning' ? 'text-amber-600' :
                      'text-blue-600';

    const icon = type === 'success' ? 'check-circle' :
                 type === 'error' ? 'alert-circle' :
                 type === 'warning' ? 'alert-triangle' :
                 'info';

    toast.className = `toast-enter flex items-center space-x-3 max-w-sm ${bgColor} border rounded-xl shadow-2xl p-4 cursor-pointer`;
    toast.innerHTML = `
        <div class="flex-shrink-0 w-8 h-8 bg-white/50 rounded-full flex items-center justify-center">
            <i data-lucide="${icon}" class="w-4 h-4 ${iconColor}"></i>
        </div>
        <p class="text-sm font-semibold flex-1">${message}</p>
        <button class="opacity-50 hover:opacity-100">
            <i data-lucide="x" class="w-4 h-4"></i>
        </button>
    `;

    toast.addEventListener('click', function () {
        toast.remove();
    });

    container.appendChild(toast);

    // Re-initialize lucide icons for new elements
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }

    // Auto dismiss after 5 seconds
    setTimeout(function () {
        toast.classList.remove('toast-enter');
        toast.classList.add('toast-exit');
        setTimeout(function () {
            toast.remove();
        }, 300);
    }, 5000);
}

/**
 * Confirmation dialog
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/**
 * Initialize Lucide icons
 */
document.addEventListener('DOMContentLoaded', function () {
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});

/**
 * Alpine.js toast manager component
 */
function toastManager() {
    return {
        showSuccess: false,
        showError: false,
        successMsg: '',
        errorMsg: '',
        init() {
            const successEl = document.querySelector('[data-success-message]');
            const errorEl = document.querySelector('[data-error-message]');

            if (successEl) {
                this.successMsg = successEl.getAttribute('data-success-message');
                if (this.successMsg) {
                    this.showSuccess = true;
                    setTimeout(() => { this.showSuccess = false; }, 5000);
                }
            }

            if (errorEl) {
                this.errorMsg = errorEl.getAttribute('data-error-message');
                if (this.errorMsg) {
                    this.showError = true;
                    setTimeout(() => { this.showError = false; }, 5000);
                }
            }
        }
    };
}

/**
 * Auto-initialize icons after any Alpine.js updates
 */
document.addEventListener('alpine:initialized', function () {
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});
