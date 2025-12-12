import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  success(title: string, text?: string): Promise<SweetAlertResult<any>> {
    return Swal.fire({ icon: 'success', title, text });
  }

  error(title: string, text?: string): Promise<SweetAlertResult<any>> {
    return Swal.fire({ icon: 'error', title, text });
  }

  info(title: string, text?: string): Promise<SweetAlertResult<any>> {
    return Swal.fire({ icon: 'info', title, text });
  }

  confirm(title: string, text?: string, confirmButtonText = 'Sí', cancelButtonText = 'Cancelar') {
    return Swal.fire({
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText,
      cancelButtonText
    });
  }
}
