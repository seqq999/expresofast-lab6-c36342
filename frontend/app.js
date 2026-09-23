const API_ROOT = "http://localhost:8080/api";
const API_ENVIOS = `${API_ROOT}/envios`;
const API_VEHICULOS = `${API_ROOT}/vehiculos`;
const API_CONDUCTORES = `${API_ROOT}/conductores`;
const API_LOGIN = `${API_ROOT}/auth/login`;

function getToken() {
    return sessionStorage.getItem("jwt_token");
}

function saveSession(token) {
    sessionStorage.setItem("jwt_token", token);
    const payload = decodeJwt(token);
    if (payload) {
        const roles = extractRoles(payload);
        sessionStorage.setItem("auth_roles", JSON.stringify(roles));
        sessionStorage.setItem(
            "auth_user",
            payload.sub || payload.username || payload.usuario || "Usuario"
        );
    }
}

function clearSession() {
    sessionStorage.removeItem("jwt_token");
    sessionStorage.removeItem("auth_roles");
    sessionStorage.removeItem("auth_user");
}

function decodeJwt(token) {
    try {
        const payloadBase64 = token.split(".")[1];
        const normalized = payloadBase64.replace(/-/g, "+").replace(/_/g, "/");
        const json = decodeURIComponent(
            atob(normalized)
                .split("")
                .map(char => "%" + char.charCodeAt(0).toString(16).padStart(2, "0"))
                .join("")
        );
        return JSON.parse(json);
    } catch (error) {
        return null;
    }
}

function extractRoles(payload) {
    if (Array.isArray(payload.roles)) return payload.roles;
    if (Array.isArray(payload.authorities)) {
        return payload.authorities.map(item =>
            typeof item === "string" ? item : item.authority
        );
    }
    if (typeof payload.roles === "string") return payload.roles.split(",");
    return [];
}

function getRoles() {
    try {
        return JSON.parse(sessionStorage.getItem("auth_roles") || "[]");
    } catch (error) {
        return [];
    }
}

function getUserName() {
    return sessionStorage.getItem("auth_user") || "Usuario";
}

function hasRole(role) {
    return getRoles().includes(role);
}

async function request(url, options = {}) {
    const token = getToken();
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    if (token) headers.Authorization = `Bearer ${token}`;

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401 || response.status === 403) {
        clearSession();
        window.location.href = "index.html";
        throw new Error("Sesión expirada o sin permisos.");
    }

    if (response.status === 400) {
        const body = await safeJson(response);
        const detalle = extractProblemDetails(body);
        throw new Error(detalle);
    }

    if (!response.ok) {
        const body = await safeJson(response);
        throw new Error((body && body.detail) || `La solicitud falló (${response.status}).`);
    }

    if (response.status === 204) return null;
    return safeJson(response);
}

async function safeJson(response) {
    try {
        return await response.json();
    } catch (error) {
        return null;
    }
}

function extractProblemDetails(body) {
    if (!body) return "Solicitud inválida.";
    if (Array.isArray(body.errors) && body.errors.length) {
        return body.errors.map(err => err.message || err.detail || err).join(" · ");
    }
    return body.detail || body.title || "Solicitud inválida.";
}

function initLoginPage() {
    const form = document.querySelector("#loginForm");
    const errorBox = document.querySelector("#login-error");
    const submitButton = document.querySelector("#login-button");

    // Si ya hay sesión activa, saltar directo al dashboard
    if (getToken()) {
        window.location.href = "dashboard.html";
        return;
    }

    form.addEventListener("submit", async event => {
        event.preventDefault();
        hideError();
        submitButton.disabled = true;

        const username = document.querySelector("#username").value.trim();
        const password = document.querySelector("#password").value;

        try {
            const response = await fetch(API_LOGIN, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                const body = await safeJson(response);
                const message =
                    response.status === 401
                        ? "Usuario o contraseña incorrectos."
                        : extractProblemDetails(body);
                throw new Error(message);
            }

            const data = await response.json();
            const token = data.token || data.jwt || data.accessToken;
            if (!token) throw new Error("La respuesta del servidor no incluyó un token.");

            saveSession(token);
            window.location.href = "dashboard.html";
        } catch (error) {
            showError(error.message || "No se pudo iniciar sesión.");
        } finally {
            submitButton.disabled = false;
        }
    });

    function showError(message) {
        errorBox.textContent = message;
        errorBox.hidden = false;
    }

    function hideError() {
        errorBox.textContent = "";
        errorBox.hidden = true;
    }
}

function initDashboardPage() {
    if (!getToken()) {
        window.location.href = "index.html";
        return;
    }

    const roles = getRoles();
    const isAdmin = roles.includes("ROLE_ADMIN");
    const isOperador = roles.includes("ROLE_OPERADOR");
    const isConductor = roles.includes("ROLE_CONDUCTOR");

    const state = { envios: [], filter: "TODOS" };

    const el = {
        userName: document.querySelector("#user-name"),
        userRole: document.querySelector("#user-role"),
        logout: document.querySelector("#logout-button"),
        feedback: document.querySelector("#feedback"),
        enviosGrid: document.querySelector("#enviosGrid"),
        emptyState: document.querySelector("#empty-state"),
        kpiTotal: document.querySelector("#kpi-total-envios"),
        kpiVehiculos: document.querySelector("#kpi-vehiculos-activos"),
        kpiEntregados: document.querySelector("#kpi-paquetes-entregados"),
        roleActions: document.querySelector("#role-actions"),
        btnNuevoVehiculo: document.querySelector("#btn-nuevo-vehiculo"),
        auditPanel: document.querySelector("#audit-panel"),
        auditList: document.querySelector("#audit-list"),
        createPanel: document.querySelector("#create-panel"),
        shipmentForm: document.querySelector("#shipment-form"),
        submitButton: document.querySelector("#submit-button"),
        vehicleSelect: document.querySelector("#vehiculo-id"),
        driverSelect: document.querySelector("#conductor-id")
    };

    //cabecera de usuario
    el.userName.textContent = getUserName();
    el.userRole.textContent = roles[0] || "";

    //visibilidad según rol
    el.auditPanel.hidden = !isAdmin;
    el.roleActions.hidden = !(isAdmin || isOperador);
    el.btnNuevoVehiculo.hidden = !isAdmin;
    el.createPanel.hidden = !(isAdmin || isOperador);

    el.logout.addEventListener("click", () => {
        clearSession();
        window.location.href = "index.html";
    });

    if (el.shipmentForm) {
        el.shipmentForm.addEventListener("submit", createEnvio);
    }

    document.querySelectorAll("[data-filter]").forEach(button => {
        button.addEventListener("click", () => {
            document.querySelectorAll("[data-filter]").forEach(b => b.classList.remove("is-active"));
            button.classList.add("is-active");
            state.filter = button.dataset.filter;
            renderEnvios();
        });
    });

    async function loadEnvios() {
        setFeedback("");
        try {
            state.envios = await request(`${API_ENVIOS}/optimizados`);
            renderKpis();
            renderEnvios();
            if (isAdmin) loadAuditoriaGlobal();
        } catch (error) {
            setFeedback(error.message, "error");
        }
    }

    async function loadCatalogs() {
        try {
            const [vehiculos, conductores] = await Promise.all([
                request(API_VEHICULOS),
                request(API_CONDUCTORES)
            ]);
            fillSelect(el.vehicleSelect, vehiculos, v => v.placa || `Vehículo ${v.id}`);
            fillSelect(el.driverSelect, conductores, c =>
                `${c.nombre || ""} ${c.apellidos || ""}`.trim() || `Conductor ${c.id}`
            );
        } catch (error) {
            setFeedback(`No se pudieron cargar los catálogos: ${error.message}`, "error");
        }
    }

    function fillSelect(select, items, labelFn) {
        select.innerHTML = '<option value="">Seleccione una opción</option>';
        items.forEach(item => {
            select.insertAdjacentHTML(
                "beforeend",
                `<option value="${item.id}">${escapeHtml(labelFn(item))}</option>`
            );
        });
    }

    async function createEnvio(event) {
        event.preventDefault();
        el.submitButton.disabled = true;
        setFeedback("");

        const formData = new FormData(el.shipmentForm);
        const payload = {
            codigoRastreo: formData.get("codigoRastreo"),
            direccionDestino: formData.get("direccionDestino"),
            pesoKg: Number(formData.get("pesoKg")),
            costo: Number(formData.get("costo")),
            vehiculoId: Number(formData.get("vehiculoId")),
            conductorId: Number(formData.get("conductorId"))
        };

        try {
            await request(API_ENVIOS, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            el.shipmentForm.reset();
            await loadEnvios();
            setFeedback("Envío registrado correctamente.", "success");
        } catch (error) {
            setFeedback(`No se pudo registrar el envío: ${error.message}`, "error");
        } finally {
            el.submitButton.disabled = false;
        }
    }

    function renderKpis() {
        el.kpiTotal.textContent = state.envios.length;
        el.kpiEntregados.textContent = state.envios.filter(
            e => e.estadoEnvio === "ENTREGADO"
        ).length;
        const vehiculosActivos = new Set(
            state.envios
                .filter(e => e.estadoEnvio === "EN_TRANSITO")
                .map(e => e.vehiculoId)
        );
        el.kpiVehiculos.textContent = vehiculosActivos.size;
    }

    function getVisibleEnvios() {
        let envios = state.envios;

        // ROLE_CONDUCTOR: solo ve los envíos asignados a su propio vehículo
        if (isConductor) {
            const miVehiculoId = getVehiculoIdDelConductor();
            envios = envios.filter(e => e.vehiculoId === miVehiculoId);
        }

        if (state.filter === "TODOS") return envios;
        return envios.filter(e => e.estadoEnvio === state.filter);
    }

    function getVehiculoIdDelConductor() {
        const payload = decodeJwt(getToken());
        return payload ? payload.vehiculoId : null;
    }

    function renderEnvios() {
        const visibles = getVisibleEnvios();
        el.enviosGrid.innerHTML = visibles.map(renderEnvioCard).join("");
        el.emptyState.hidden = visibles.length > 0;

        el.enviosGrid.querySelectorAll("[data-action-state]").forEach(button => {
            button.addEventListener("click", () =>
                actualizarEstado(button.dataset.id, button.dataset.actionState, button)
            );
        });
        el.enviosGrid.querySelectorAll("[data-assign-vehicle]").forEach(button => {
            button.addEventListener("click", () => asignarVehiculo(button.dataset.id, button));
        });
    }

    async function asignarVehiculo(id, button) {
        const vehiculoId = window.prompt("ID del vehículo a asignar:");
        if (!vehiculoId) return;

        button.disabled = true;
        try {
            await request(`${API_ENVIOS}/${id}/vehiculo`, {
                method: "PUT",
                body: JSON.stringify({ vehiculoId: Number(vehiculoId) })
            });
            await loadEnvios();
            setFeedback("Vehículo asignado correctamente.", "success");
        } catch (error) {
            button.disabled = false;
            setFeedback(error.message, "error");
        }
    }

    function renderEnvioCard(envio) {
        const status = envio.estadoEnvio || "SIN_ESTADO";
        const statusClass = ["PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO"].includes(status)
            ? `status-${status.toLowerCase()}`
            : "status-unknown";

        const acciones = [];

        // ROLE_OPERADOR: asignar vehículo y avanzar el envío a EN_TRANSITO
        if (isOperador && status === "PENDIENTE") {
            acciones.push(
                `<button class="action-button" type="button" data-id="${envio.id}" data-assign-vehicle="true">Asignar vehículo</button>`
            );
            acciones.push(
                `<button class="action-button" type="button" data-id="${envio.id}" data-action-state="EN_TRANSITO">Marcar en tránsito</button>`
            );
        }

        // ROLE_CONDUCTOR: solo marcar como ENTREGADO sus propios envíos en tránsito
        if (isConductor && status === "EN_TRANSITO") {
            acciones.push(
                `<button class="action-button" type="button" data-id="${envio.id}" data-action-state="ENTREGADO">Marcar entregado</button>`
            );
        }

        // ROLE_ADMIN: acceso total a ambas transiciones (además de la bitácora en el aside)
        if (isAdmin) {
            if (status === "PENDIENTE") {
                acciones.push(
                    `<button class="action-button" type="button" data-id="${envio.id}" data-action-state="EN_TRANSITO">Marcar en tránsito</button>`
                );
            }
            if (status === "PENDIENTE" || status === "EN_TRANSITO") {
                acciones.push(
                    `<button class="action-button" type="button" data-id="${envio.id}" data-action-state="ENTREGADO">Marcar entregado</button>`
                );
            }
        }

        return `
      <article class="shipment-card">
        <div class="card-header">
          <span class="tracking-code">${escapeHtml(envio.codigoRastreo || "Sin código")}</span>
          <span class="pill-status ${statusClass}">${escapeHtml(status)}</span>
        </div>
        <div class="card-route">
          <span class="route-value">${escapeHtml(envio.direccionDestino || "No indicado")}</span>
        </div>
        <div class="card-meta">
          <span>${formatNumber(envio.pesoKg)} kg</span>
          <span>${formatCurrency(envio.costo)}</span>
        </div>
        ${acciones.length ? `<div class="card-actions">${acciones.join("")}</div>` : ""}
      </article>
    `;
    }

    async function actualizarEstado(id, nuevoEstado, button) {
        button.disabled = true;
        try {
            await request(`${API_ENVIOS}/${id}/estado`, {
                method: "PATCH",
                body: JSON.stringify({ nuevoEstado })
            });
            await loadEnvios();
            setFeedback("Estado actualizado correctamente.", "success");
        } catch (error) {
            button.disabled = false;
            setFeedback(error.message, "error");
        }
    }

    async function loadAuditoriaGlobal() {
        el.auditList.innerHTML = '<li class="audit-entry">Cargando historial...</li>';
        try {
            const resultados = await Promise.all(
                state.envios.map(envio =>
                    request(`${API_ENVIOS}/${envio.id}/bitacora`)
                        .then(entradas => (entradas || []).map(e => ({ ...e, codigoRastreo: envio.codigoRastreo })))
                        .catch(() => [])
                )
            );

            const todasLasEntradas = resultados
                .flat()
                .sort((a, b) => new Date(b.fechaCambio) - new Date(a.fechaCambio));

            if (!todasLasEntradas.length) {
                el.auditList.innerHTML = '<li class="audit-entry">Sin cambios registrados todavía.</li>';
                return;
            }

            el.auditList.innerHTML = todasLasEntradas
                .map(
                    item => `
            <li class="audit-entry">
              <strong>${escapeHtml(item.codigoRastreo || "-")}: ${escapeHtml(item.estadoAnterior || "-")} → ${escapeHtml(item.estadoNuevo || "-")}</strong>
              <time>${item.fechaCambio ? new Date(item.fechaCambio).toLocaleString("es-CR") : ""}</time>
            </li>`
                )
                .join("");
        } catch (error) {
            el.auditList.innerHTML = `<li class="audit-entry">${escapeHtml(error.message)}</li>`;
        }
    }

    function setFeedback(message, type = "error") {
        el.feedback.textContent = message;
        el.feedback.classList.remove("is-error", "is-success");
        if (message) el.feedback.classList.add(type === "success" ? "is-success" : "is-error");
    }

    loadEnvios();
    if (isAdmin || isOperador) loadCatalogs();
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("es-CR", { maximumFractionDigits: 2 });
}

function formatCurrency(value) {
    return Number(value || 0).toLocaleString("es-CR", {
        style: "currency",
        currency: "CRC",
        maximumFractionDigits: 2
    });
}

document.addEventListener("DOMContentLoaded", () => {
    if (document.querySelector("#loginForm")) {
        initLoginPage();
    } else if (document.querySelector("#kpiSection")) {
        initDashboardPage();
    }
});