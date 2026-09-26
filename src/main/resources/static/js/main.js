// Ensure check-out date can never be before/equal to check-in date on any
// search or booking form on the page.
document.addEventListener("DOMContentLoaded", function () {
  const today = new Date().toISOString().split("T")[0];

  document.querySelectorAll('input[name="checkInDate"]').forEach(function (checkIn) {
    checkIn.min = today;
    const form = checkIn.closest("form");
    if (!form) return;
    const checkOut = form.querySelector('input[name="checkOutDate"]');
    if (!checkOut) return;

    function syncMin() {
      if (checkIn.value) {
        const next = new Date(checkIn.value);
        next.setDate(next.getDate() + 1);
        checkOut.min = next.toISOString().split("T")[0];
        if (checkOut.value && checkOut.value <= checkIn.value) {
          checkOut.value = checkOut.min;
        }
      }
    }
    checkIn.addEventListener("change", syncMin);
    syncMin();
  });

  // Confirm before any destructive/administrative action
  document.querySelectorAll("[data-confirm]").forEach(function (el) {
    el.addEventListener("submit", function (e) {
      if (!confirm(el.getAttribute("data-confirm"))) {
        e.preventDefault();
      }
    });
  });
});
