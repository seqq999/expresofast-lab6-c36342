const API_V1_ENVIOS = `${API_ROOT}/v1/envios`;

function initPaginacionPage() {
    if (!getToken()) {
        window.location.href = LOGIN_URL;
        return;
    }

    document.querySelector("#user-name").textContent = getUserName();
    document.querySelector("#user-role").textContent = getRoles()[0] || "";
    document.querySelector("#logout-button").addEventListener("click", () => {
        clearSession();
        window.location.href = LOGIN_URL;
    });

    const el = {
        form: document.querySelector("#filtros-form"),
        busqueda: document.querySelector("#busqueda"),
        modoConsulta: document.querySelector("#modo-consulta"),
        estadoFiltro: document.querySelector("#estado-filtro"),
        tamanoPagina: document.querySelector("#tamano-pagina"),
        tbody: document.querySelector("#envios-tbody"),
        feedback: document.querySelector("#feedback"),
        paginacionNav: document.querySelector("#paginacion"),
        pageStatus: document.querySelector("#page-status"),
        btnPrimera: document.querySelector("#btn-primera"),
        btnAnterior: document.querySelector("#btn-anterior"),
        btnSiguiente: document.querySelector("#btn-siguiente"),
        btnUltima: document.querySelector("#btn-ultima")
    };

    const state = {
        currentPage: 0,
        totalPages: 1,
        modo: "paginado"
    };

    el.form.addEventListener("submit", event => {
        event.preventDefault();
        state.currentPage = 0;
        cargarEnvios();
    });

    el.btnPrimera.addEventListener("click", () => {
        state.currentPage = 0;
        cargarEnvios();
    });

    el.btnAnterior.addEventListener("click", () => {
        if (state.currentPage > 0) {
            state.currentPage -= 1;
            cargarEnvios();
        }
    });

    el.btnSiguiente.addEventListener("click", () => {
        if (state.currentPage < state.totalPages - 1) {
            state.currentPage += 1;
            cargarEnvios();
        }
    });

    el.btnUltima.addEventListener("click", () => {
        state.currentPage = state.totalPages - 1;
        cargarEnvios();
    });

    async function cargarEnvios() {
        setFeedback("");
        el.tbody.innerHTML = `<tr><td colspan="5" class="tabla-vacia">Cargando envíos...</td></tr>`;

        const modo = el.modoConsulta.value;
        const estado = el.estadoFiltro.value;

        try {
            if (modo === "procedimiento") {
                await cargarViaStoredProcedure(estado);
            } else {
                await cargarPaginado(estado);
            }
        } catch (error) {
            el.tbody.innerHTML = `<tr><td colspan="5" class="tabla-vacia">${escapeHtml(error.message)}</td></tr>`;
            setFeedback(error.message, "error");
            el.paginacionNav.hidden = true;
        }
    }

    async function cargarPaginado(estado) {
        const params = new URLSearchParams({
            page: state.currentPage,
            size: el.tamanoPagina.value
        });

        if (el.busqueda.value.trim()) params.set("busqueda", el.busqueda.value.trim());
        if (estado) params.set("estado", estado);

        const data = await request(`${API_V1_ENVIOS}?${params.toString()}`);

        renderFilas(data.content);
        actualizarPaginador(data);
    }

    async function cargarViaStoredProcedure(estado) {
        if (!estado) {
            throw new Error("Seleccione un estado para consultar vía Stored Procedure.");
        }

        const data = await request(`${API_V1_ENVIOS}/procedimiento/${estado}`);

        renderFilas(data);
        el.paginacionNav.hidden = true;
    }

    function renderFilas(envios) {
        if (!envios || envios.length === 0) {
            el.tbody.innerHTML = `<tr><td colspan="5" class="tabla-vacia">No se encontraron envíos.</td></tr>`;
            return;
        }

        el.tbody.innerHTML = envios
            .map(
                envio => `
      <tr>
        <td>${escapeHtml(envio.codigoRastreo || "-")}</td>
        <td>${escapeHtml(envio.destinatario || "-")}</td>
        <td>${escapeHtml(envio.direccionDestino || "-")}</td>
        <td>${formatCurrency(envio.montoFlete)}</td>
        <td><span class="pill-status status-${(envio.estado || "").toLowerCase()}">${escapeHtml(envio.estado || "-")}</span></td>
      </tr>`
            )
            .join("");
    }

    function actualizarPaginador(data) {
        state.currentPage = data.number;
        state.totalPages = data.totalPages;

        el.paginacionNav.hidden = false;

        const paginaVisible = data.totalPages === 0 ? 0 : data.number + 1;
        el.pageStatus.textContent = `Página ${paginaVisible} de ${data.totalPages} (Total: ${data.totalElements} envíos)`;

        el.btnPrimera.disabled = data.first;
        el.btnAnterior.disabled = data.first;
        el.btnSiguiente.disabled = data.last;
        el.btnUltima.disabled = data.last;
    }

    function setFeedback(message, type = "error") {
        el.feedback.textContent = message;
        el.feedback.classList.remove("is-error", "is-success");
        if (message) el.feedback.classList.add(type === "success" ? "is-success" : "is-error");
    }

    cargarEnvios();
}

document.addEventListener("DOMContentLoaded", () => {
    if (document.querySelector("#filtros-form")) {
        initPaginacionPage();
    }
});