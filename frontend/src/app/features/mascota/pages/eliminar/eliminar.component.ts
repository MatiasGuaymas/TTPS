import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../mascota.service';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';

@Component({
  selector: 'app-eliminar',
  imports: [],
  templateUrl: './eliminar.component.html',
  styleUrl: './eliminar.component.css',
})
export class PetDeleteComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private authService = inject(AuthService);
  private alert = inject(AlertService);

  petId!: number;

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.petId = Number(idParam);
      this.solicitarConfirmacion();
    }
  }

  solicitarConfirmacion() {
    this.alert.confirm(
      '¿Estás seguro?',
      'Esta acción eliminará permanentemente la mascota. No podrás deshacer esto.',
      'Sí, eliminar',
      'Cancelar'
    ).then((result) => {
      if (result.isConfirmed) {
        this.deletePet();
      } else {
        this.cancel();
      }
    });
  }

  private deletePet() {
    const token = localStorage.getItem('token') || "";

    this.mascotaService.deletePet(this.petId, token).subscribe({
      next: () => {
        this.alert.success('Eliminado', 'La mascota ha sido eliminada exitosamente.')
          .then(() => {
            this.router.navigate(['/listado-mascotas']);
          });
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Hubo un error al intentar eliminar la mascota.';
        this.alert.error('Error', errorMsg).then(() => {
          this.goToDetail();
        });
      }
    });
  }

  cancel() {
    this.goToDetail();
  }

  private goToDetail() {
    this.router.navigate(['/mascota', this.petId]);
  }
}
