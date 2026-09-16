const API_ROOT = "http://localhost:8080/api";
const form = document.querySelector("#login-form");
const feedback = document.querySelector("#login-feedback");

form.addEventListener("submit", async event => {
    event.preventDefault();
    const data = Object.fromEntries(new FormData(form));
    feedback.textContent = "Validando acceso...";
    try {
        const response = await fetch(`${API_ROOT}/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(data) });
        const result = await response.json();
        if (!response.ok) throw new Error(result.error || "No se pudo iniciar sesión");
        localStorage.setItem("jwt_token", result.token);
        localStorage.setItem("auth_user", result.username);
        localStorage.setItem("auth_roles", JSON.stringify(result.roles || []));
        window.location.href = "index.html";
    } catch (error) { feedback.textContent = error.message; }
});