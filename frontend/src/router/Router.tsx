import { BrowserRouter, Route, Routes } from "react-router-dom"
import { LoginView } from "../views/auth/LoginView"

export const Router = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LoginView />}></Route>
                <Route path="/index" element={<h1> Bienvenido, ha iniciado sesion </h1>}></Route>
            </Routes>
        </BrowserRouter>
    )
}
