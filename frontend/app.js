const API_ROOT = "http://localhost:8080/api";
const API_BASE = `${API_ROOT}/envios`;
const token = localStorage.getItem("jwt_token");
if (!token) window.location.href = "login.html";
const roles = JSON.parse(localStorage.getItem("auth_roles") || "[]");
const isConductor = roles.includes("ROLE_CONDUCTOR");
const canAudit = roles.includes("ROLE_ADMIN") || roles.includes("ROLE_OPERADOR");

const state = {
    shipments: [],
    filter: "TODOS",
    search: ""
};

const elements = {
    grid: document.querySelector("#shipments-grid"),
    empty: document.querySelector("#empty-state"),
    feedback: document.querySelector("#feedback"),
    form: document.querySelector("#shipment-form"),
    submitButton: document.querySelector("#submit-button"),
    vehicleSelect: document.querySelector("#vehiculo-id"),
    driverSelect: document.querySelector("#conductor-id"),
    search: document.querySelector("#search-input"),
    refresh: document.querySelector("#refresh-button"),
    sync: document.querySelector("#last-sync"),
    connectionLabel: document.querySelector("#connection-label"),
    connectionDot: document.querySelector(".status-dot"),
    userBadge: document.querySelector("#user-badge"),
    logout: document.querySelector("#logout-button"),
    createPanel: document.querySelector(".create-panel"),
    auditModal: document.querySelector("#audit-modal"),
    auditList: document.querySelector("#audit-list"),
    auditFrom: document.querySelector("#audit-from"),
    auditTo: document.querySelector("#audit-to"),
    filterCount: document.querySelector("#filter-count"),
    metrics: {
        total: document.querySelector("#metric-total"),
        pending: document.querySelector("#metric-pending"),
        transit: document.querySelector("#metric-transit"),
        delivered: document.querySelector("#metric-delivered")
    }
};

async function request(url, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    if (token) headers.Authorization = `Bearer ${token}`;
    const response = await fetch(url, {
        headers,
        ...options
    });

    if (response.status === 401) {
        localStorage.clear();
        window.location.href = "login.html";
        throw new Error("Sesión expirada");
    }

    if (response.status === 403) {
        throw new Error("No tiene permisos para esta operación");
    }

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `La solicitud fallo (${response.status})`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

async function loadShipments() {
    setFeedback("");
    setConnection("loading");

    try {
        state.shipments = await request(`${API_BASE}/optimizados`);
        renderDashboard();
        elements.sync.textContent = new Date().toLocaleTimeString("es-CR", {
            hour: "2-digit",
            minute: "2-digit"
        });
        setConnection("online");
    } catch (error) {
        state.shipments = [];
        renderDashboard();
        setFeedback(`No se pudieron cargar los envios: ${error.message}`);
        setConnection("offline");
    }
}

async function loadCatalogs() {
    try {
        const [vehicles, drivers] = await Promise.all([
            request(`${API_ROOT}/vehiculos`),
            request(`${API_ROOT}/conductores`)
        ]);
        fillVehicleSelect(vehicles);
        fillDriverSelect(drivers);
    } catch (error) {
        setFeedback(`No se pudieron cargar los catálogos: ${error.message}`);
    }
}

function fillVehicleSelect(vehicles) {
    elements.vehicleSelect.innerHTML = '<option value="">Seleccione un vehiculo</option>';
    vehicles.forEach(vehicle => {
        const company = vehicle.empresa?.nombre ? ` - ${vehicle.empresa.nombre}` : "";
        elements.vehicleSelect.insertAdjacentHTML(
            "beforeend",
            `<option value="${vehicle.id}">${escapeHtml(vehicle.placa || `Vehiculo ${vehicle.id}`)}${escapeHtml(company)}</option>`
        );
    });
}

function fillDriverSelect(drivers) {
    elements.driverSelect.innerHTML = '<option value="">Seleccione un conductor</option>';
    drivers.forEach(driver => {
        const name = `${driver.nombre || ""} ${driver.apellidos || ""}`.trim();
        elements.driverSelect.insertAdjacentHTML(
            "beforeend",
            `<option value="${driver.id}">${escapeHtml(name || `Conductor ${driver.id}`)}</option>`
        );
    });
}

function renderDashboard() {
    updateMetrics();
    updateFilterCounts();
    renderShipments();
}

function updateMetrics() {
    const count = status => state.shipments.filter(shipment => shipment.estadoEnvio === status).length;
    elements.metrics.total.textContent = state.shipments.length;
    elements.metrics.pending.textContent = count("PENDIENTE");
    elements.metrics.transit.textContent = count("EN_TRANSITO");
    elements.metrics.delivered.textContent = count("ENTREGADO");
}

function updateFilterCounts() {
    const statuses = ["TODOS", "PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO"];
    elements.filterCount.textContent = getVisibleShipments().length;

    statuses.forEach(status => {
        const count = status === "TODOS"
            ? state.shipments.length
            : state.shipments.filter(shipment => shipment.estadoEnvio === status).length;
        const element = document.querySelector(`[data-filter-count="${status}"]`);
        if (element) {
            element.textContent = count;
        }
    });
}

function getVisibleShipments() {
    const search = state.search.toLowerCase();
    return state.shipments.filter(shipment => {
        const matchesFilter = state.filter === "TODOS" || shipment.estadoEnvio === state.filter;
        const searchableText = `${shipment.codigoRastreo || ""} ${shipment.direccionDestino || ""}`.toLowerCase();
        return matchesFilter && searchableText.includes(search);
    });
}

function renderShipments() {
    const visibleShipments = getVisibleShipments();
    elements.grid.innerHTML = visibleShipments.map(renderShipmentCard).join("");
    elements.empty.hidden = visibleShipments.length > 0;

    elements.grid.querySelectorAll("[data-action-state]").forEach(button => {
        button.addEventListener("click", () => updateShipmentState(button.dataset.id, button.dataset.actionState, button));
    });
    elements.grid.querySelectorAll("[data-audit-id]").forEach(button => {
        button.addEventListener("click", () => openAudit(button.dataset.auditId, button.dataset.auditCode));
    });
}

function renderShipmentCard(shipment) {
    const status = shipment.estadoEnvio || "SIN ESTADO";
    const normalizedStatus = status.toLowerCase();
    const isPending = status === "PENDIENTE";
    const isTransit = status === "EN_TRANSITO";
    const statusClass = ["PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO"].includes(status)
        ? `status-${normalizedStatus}`
        : "status-unknown";

    return `
        <article class="shipment-card">
            <div class="card-header">
                <span class="tracking-code">${escapeHtml(shipment.codigoRastreo || "Sin codigo")}</span>
                <span class="pill-status ${statusClass}">${formatStatus(status)}</span>
            </div>
            <div class="card-route">
                <div>
                    <span class="route-label">Destino</span>
                    <p class="route-value">${escapeHtml(shipment.direccionDestino || "No indicado")}</p>
                </div>
                <span class="route-arrow" aria-hidden="true">&#8594;</span>
            </div>
            <div class="card-meta">
                <div>
                    <span class="meta-label">Peso</span>
                    <p class="meta-value">${formatNumber(shipment.pesoKg)} kg</p>
                </div>
                <div>
                    <span class="meta-label">Costo</span>
                    <p class="meta-value">${formatCurrency(shipment.costo)}</p>
                </div>
                <div>
                    <span class="meta-label">Asignacion</span>
                    <p class="meta-value">V-${shipment.vehiculoId ?? "-"} / C-${shipment.conductorId ?? "-"}</p>
                </div>
                <div>
                    <span class="meta-label">Empresa</span>
                    <p class="meta-value">${escapeHtml(shipment.empresaNombre || "No indicada")}</p>
                </div>
            </div>
            <div class="card-actions">
                ${isPending ? `<button class="action-button" type="button" data-id="${shipment.id}" data-action-state="EN_TRANSITO">Marcar en transito</button>` : ""}
                ${isPending || isTransit ? `<button class="action-button" type="button" data-id="${shipment.id}" data-action-state="ENTREGADO">Marcar entregado</button>` : ""}
                ${canAudit ? `<button class="action-button" type="button" data-audit-id="${shipment.id}" data-audit-code="${escapeHtml(shipment.codigoRastreo || "")}">Ver bitácora</button>` : ""}
            </div>
        </article>
    `;
}

async function updateShipmentState(id, status, button) {
    button.disabled = true;
    setFeedback("");

    try {
        await request(`${API_BASE}/${id}/estado`, { method: "PATCH", body: JSON.stringify({ nuevoEstado: status }) });
        await loadShipments();
        setFeedback("Estado actualizado correctamente.", "success");
    } catch (error) {
        button.disabled = false;
        setFeedback(`No se pudo actualizar el estado: ${error.message}`);
    }
}

async function createShipment(event) {
    event.preventDefault();
    elements.submitButton.disabled = true;
    setFeedback("");

    const formData = new FormData(elements.form);
    const payload = {
        codigoRastreo: formData.get("codigoRastreo"),
        direccionDestino: formData.get("direccionDestino"),
        pesoKg: Number(formData.get("pesoKg")),
        costo: Number(formData.get("costo")),
        vehiculoId: Number(formData.get("vehiculoId")),
        conductorId: Number(formData.get("conductorId"))
    };

    try {
        await request(API_BASE, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        elements.form.reset();
        await loadShipments();
        setFeedback("Envio registrado correctamente.", "success");
    } catch (error) {
        setFeedback(`No se pudo registrar el envio: ${error.message}`);
    } finally {
        elements.submitButton.disabled = false;
    }
}

async function openAudit(id, code) {
    elements.auditModal.hidden = false;
    document.querySelector("#audit-title").textContent = `Bitácora ${code}`;
    elements.auditList.innerHTML = "Cargando historial...";
    try {
        state.audit = await request(`${API_BASE}/${id}/bitacora`);
        renderAudit();
    } catch (error) { elements.auditList.textContent = error.message; }
}

function renderAudit() {
    const from = elements.auditFrom.value ? new Date(`${elements.auditFrom.value}T00:00:00`) : null;
    const to = elements.auditTo.value ? new Date(`${elements.auditTo.value}T23:59:59`) : null;
    const entries = (state.audit || []).filter(item => {
        const date = new Date(item.fechaCambio);
        return (!from || date >= from) && (!to || date <= to);
    });
    elements.auditList.innerHTML = entries.length
        ? entries.map(item => `<article class="audit-entry"><strong>${escapeHtml(item.estadoAnterior)} → ${escapeHtml(item.estadoNuevo)}</strong><time>${new Date(item.fechaCambio).toLocaleString("es-CR")}</time><span>${escapeHtml(item.usuario || "-")}</span><p>${escapeHtml(item.observaciones || "Sin observaciones")}</p></article>`).join("")
        : "No hay cambios en ese rango.";
}

function setConnection(status) {
    const labels = { loading: "Conectando", online: "API conectada", offline: "API desconectada" };
    elements.connectionLabel.textContent = labels[status];
    elements.connectionDot.className = `status-dot is-${status}`;
}

function setFeedback(message, type = "error") {
    elements.feedback.textContent = message;
    elements.feedback.style.color = type === "success" ? "var(--green)" : "var(--red)";
}

function formatStatus(status) {
    return { EN_TRANSITO: "EN TRANSITO", PENDIENTE: "PENDIENTE", ENTREGADO: "ENTREGADO", CANCELADO: "CANCELADO" }[status] || status;
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("es-CR", { maximumFractionDigits: 2 });
}

function formatCurrency(value) {
    return Number(value || 0).toLocaleString("es-CR", { style: "currency", currency: "CRC", maximumFractionDigits: 2 });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

document.querySelectorAll("[data-filter]").forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll("[data-filter]").forEach(item => item.classList.remove("is-active"));
        button.classList.add("is-active");
        state.filter = button.dataset.filter;
        updateFilterCounts();
        renderShipments();
    });
});

elements.search.addEventListener("input", event => {
    state.search = event.target.value.trim();
    updateFilterCounts();
    renderShipments();
});
elements.refresh.addEventListener("click", loadShipments);
elements.form.addEventListener("submit", createShipment);
elements.userBadge.textContent = `${localStorage.getItem("auth_user") || "Usuario"} · ${roles.join(", ")}`;
elements.createPanel.hidden = isConductor;
elements.logout.addEventListener("click", () => { localStorage.clear(); window.location.href = "login.html"; });
document.querySelector("#close-audit").addEventListener("click", () => { elements.auditModal.hidden = true; });
elements.auditModal.addEventListener("click", event => { if (event.target === elements.auditModal) elements.auditModal.hidden = true; });
elements.auditFrom.addEventListener("change", renderAudit);
elements.auditTo.addEventListener("change", renderAudit);
loadShipments();
if (!isConductor) loadCatalogs();
