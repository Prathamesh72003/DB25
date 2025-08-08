import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res = await fetch("http://localhost:5000/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) throw new Error("Invalid email or password");

      const data = await res.json();

      // Store user & token in sessionStorage
      sessionStorage.setItem("user", JSON.stringify(data.user));
      sessionStorage.setItem("token", data.token);

      navigate("/dashboard");
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleLogin}>
        <h2 className="login-title">Login</h2>

        <input
          type="email"
          className="login-input"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          required
        />

        <input
          type="password"
          className="login-input"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          required
        />

        {error && <p className="login-error">{error}</p>}

        <button type="submit" className="login-button">
          Login
        </button>
      </form>
    </div>


    ---

    /* Page background */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  font-family: Arial, sans-serif;
}

/* Form styling */
.login-form {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.15);
  width: 320px;
  text-align: center;
}

/* Title */
.login-title {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.8rem;
}

/* Input fields */
.login-input {
  width: 100%;
  padding: 0.75rem;
  margin: 0.5rem 0;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.login-input:focus {
  border-color: #4caf50;
}

/* Error message */
.login-error {
  color: red;
  font-size: 0.9rem;
  margin: 0.5rem 0;
}

/* Button */
.login-button {
  width: 100%;
  padding: 0.75rem;
  margin-top: 1rem;
  background: #4caf50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.login-button:hover {
  background: #43a047;
}

  );
}
