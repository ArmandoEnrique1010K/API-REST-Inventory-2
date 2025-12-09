import { object, email, pipe, string, minLength, InferOutput } from 'valibot';

// Esquema de validación
export const AuthSchema = object({
    email: pipe(string(), email('El correo electrónico no es válido')),
    password: pipe(
        string(),
        minLength(8, 'La contraseña debe tener al menos 8 caracteres')
    ),
});


// Tipo inferido del esquema
export type AuthData = InferOutput<typeof AuthSchema>;
