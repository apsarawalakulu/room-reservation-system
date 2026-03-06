document.addEventListener('DOMContentLoaded', async () => {

    async function loadDashboardData() {
        try {
            const data = await Api.request('/dashboard');

            document.getElementById('statTotalToday').textContent = data.totalReservationsToday;
            document.getElementById('statOccupied').textContent = data.occupiedRoomsCount;
            document.getElementById('statUpcoming').textContent = data.upcomingCheckIns;

            renderRecent(data.recentReservations);
        } catch (err) {
            console.error("Failed to load dashboard data", err);
        }
    }

    function renderRecent(reservations) {
        const tbody = document.querySelector('#recentReservationsTable tbody');
        tbody.innerHTML = '';

        if (!reservations || reservations.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No recent reservations.</td></tr>';
            return;
        }

        reservations.forEach(res => {
            const tr = document.createElement('tr');

            let statusClass = 'status-active';
            if (res.status === 'Cancelled') statusClass = 'status-cancelled';
            else if (res.status === 'Checked Out') statusClass = 'status-checkout';

            tr.innerHTML = `
                <td><a href="reservation-detail.html?id=${res.reservationNumber}" style="color: var(--secondary-color); text-decoration: none; font-weight: 500;">${res.reservationNumber}</a></td>
                <td>${res.guest.fullName}</td>
                <td>${res.roomNumber} (${res.room.roomType})</td>
                <td>${res.checkInDate}</td>
                <td>${res.checkOutDate}</td>
                <td><span class="status-badge ${statusClass}">${res.status}</span></td>
            `;
            tbody.appendChild(tr);
        });
    }

    loadDashboardData();
});
