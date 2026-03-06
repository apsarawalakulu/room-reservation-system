document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const reservationId = urlParams.get('id');

    if (!reservationId) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = 'API Blocked: No ID reference exists on physical document map context.';
        return;
    }

    try {
        const bill = await Api.request(`/bill?reservationId=${reservationId}`);

        document.getElementById('loading').style.display = 'none';
        document.getElementById('billContent').style.display = 'block';

        document.getElementById('billGuestName').textContent = bill.guestName;
        document.getElementById('billGuestAddress').textContent = bill.guestAddress;
        document.getElementById('billResNum').textContent = bill.reservationNumber;
        document.getElementById('billDate').textContent = bill.generatedDate;

        document.getElementById('billRoomType').textContent = bill.roomType;
        document.getElementById('billRoomNum').textContent = bill.roomNumber;
        document.getElementById('billCheckIn').textContent = bill.checkIn;
        document.getElementById('billCheckOut').textContent = bill.checkOut;

        document.getElementById('billNights').textContent = bill.numberOfNights;

        const formatCurrency = (val) => Number(val).toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

        document.getElementById('billRate').textContent = formatCurrency(bill.nightlyRate);
        document.getElementById('billLineTotal').textContent = formatCurrency(bill.subTotal);

        document.getElementById('billSubtotal').textContent = formatCurrency(bill.subTotal);
        document.getElementById('billTax').textContent = formatCurrency(bill.tax);
        document.getElementById('billGrandTotal').textContent = formatCurrency(bill.grandTotal);

    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = e.data && e.data.error ? e.data.error : 'Network API interruption pulling bill data array.';
    }
});
