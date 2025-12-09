import api from "../lib/axios"
import { isAxiosError } from "axios"
import { AuthData } from '../types/AuthTypes';

export const authenticateUser = async (formData: AuthData) => {
    try {
        const url = "/auth/login"
        const { data } = await api.post<string>(url, formData)
        return data
    } catch (error) {
        if (isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message)


        }
    }
}
