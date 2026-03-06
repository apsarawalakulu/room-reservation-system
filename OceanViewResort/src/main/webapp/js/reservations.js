document.addEventListener('DOMContentLoaded', () => {
    const tbody = document.querySelector('#reservationsTable tbody');
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const searchBtn = document.getElementById('searchBtn');
    const clearBtn = document.getElementById('clearBtn');

    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const pageInfo = document.getElementById('pageInfo');

    let currentPage = 1;

    async function fetchReservations() {
        const query = searchInput.value.trim();
        const status = statusFilter.value;

        try {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">Loading...</td></tr>';

            const params = new URLSearchParams();
            if (query) params.append('search', query);
            if (status !== 'All') params.append('status', status);
            params.append('page', currentPage);

            const res = await Api.request(`/reservations?${params.toString()}`);

            tbody.innerHTML = '';

            if (res.data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">No reservations found matching criteria.</td></tr>';
            } else {
                res.data.forEach(item => {
                    const tr = document.createElement('tr');
                    let statusClass = 'status-active';
                    if (item.status === 'Cancelled') statusClass = 'status-cancelled';
                    else if (item.status === 'Checked Out') statusClass = 'status-checkout';

                    tr.innerHTML = `
                        <td><a href="reservation-detail.html?id=${item.reservationNumber}" style="color: var(--secondary-color); text-decoration: none; font-weight: 500;">${item.reservationNumber}</a></td>
                        <td>${item.guest.fullName}</td>
                        <td>${item.roomNumber} (${item.room.roomType})</td>
                        <td>${item.checkInDate}</td>
                        <td>${item.checkOutDate}</td>
                        <td><span class="status-badge ${statusClass}">${item.status}</span></td>
                        <td><a href="reservation-detail.html?id=${item.reservationNumber}" class="btn btn-secondary" style="padding: 0.4rem 0.8rem; font-size: 0.875rem;">View Details</a></td>
                    `;
                    tbody.appendChild(tr);
                });
            }

            const totalPages = Math.ceil(res.total / res.limit);
            pageInfo.textContent = `Page ${res.page} of ${totalPages || 1} (${res.total} total records)`;

            prevBtn.disabled = (res.page <= 1);
            nextBtn.disabled = (res.page >= totalPages);

        } catch (e) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: red;">Failed to load data from server.</td></tr>';
        }
    }

    searchBtn.addEventListener('click', () => {
        currentPage = 1;
        fetchReservations();
    });

    clearBtn.addEventListener('click', () => {
        searchInput.value = '';
        statusFilter.value = 'All';
        currentPage = 1;
        fetchReservations();
    });

    prevBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            fetchReservations();
        }
    });

    nextBtn.addEventListener('click', () => {
        currentPage++;
        fetchReservations();
    });

    fetchReservations();
});
