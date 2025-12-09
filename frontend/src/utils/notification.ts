import { Store } from 'react-notifications-component';

export const showNotification = ({ title, message, type }: { title: string, message: string, type: "success" | "danger" | "info" }) => {
    Store.addNotification({
        title,
        message,
        type,
        insert: "top",
        container: "top-right",
        animationIn: ["animate__animated", "animate__fadeIn"],
        animationOut: ["animate__animated", "animate__fadeOut"],
        dismiss: {
            duration: 5000,
            onScreen: true
        }
    });
}