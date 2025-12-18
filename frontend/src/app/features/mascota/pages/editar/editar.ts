import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../mascota.service';
import { AlertService } from '../../../../core/services/alert.service';
import { PetUpdate, Size, State, TipoMascota } from '../../mascota.model';

@Component({
  selector: 'app-editar',
  imports: [ReactiveFormsModule],
  templateUrl: './editar.html',
  styleUrl: './editar.css',
})
export class Editar implements OnInit{
    private fb = inject(FormBuilder);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private petService = inject(MascotaService);
    private alerts = inject(AlertService);

    tiposMascota = Object.values(TipoMascota);
    estadosMascota = Object.values(State);
    tamanosMascota = Object.values(Size);

    petForm: FormGroup;
    petId: number | null = null;
    loading = signal(true);
    imagePreview = signal<string | null>(null);

    constructor() {
        this.petForm = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(2)]],
            description: ['', [Validators.required]],
            color: ['', [Validators.required]],
            size: ['', [Validators.required]],
            race: ['', [Validators.required]],
            weight: [0, [Validators.required, Validators.min(0)]],
            type: ['', [Validators.required]],
            state: ['', [Validators.required]],
            latitude: [null, [Validators.required]],
            longitude: [null, [Validators.required]],
            photoBase64: ['', [Validators.required]]
        });
    }

    ngOnInit() {
        const id=this.route.snapshot.paramMap.get('id');
        if(id){
            this.petId=+id;
            this.loadPetData(this.petId);
        }
    }
    loadPetData(petId: number) {
        this.petService.getPetById(petId).subscribe({
            next: (pet) => {
                this.petForm.patchValue({
                    ...pet,
                    photoBase64: pet.photosBase64?.[0]||''
                });
                this.imagePreview.set(pet.photosBase64?.[0] || null);
                this.loading.set(false);
            },
            error: () => {
                this.alerts.error('Error', 'No se pudo encontrar a la mascota');
                this.router.navigate(['/listado-mascotas']);
            }
        });
    }

    onFileSelected(event: any) {
        const file: File = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = () => {
                const base64String = reader.result as string;
                this.petForm.patchValue({ photoBase64: base64String });
                this.imagePreview.set(base64String);
            };
            reader.readAsDataURL(file);
        }
    }

    onLocationChanged(coords: { latitude: number; longitude: number }) {
        this.petForm.patchValue({
            latitude: coords.latitude,
            longitude: coords.longitude
        });
    }

    onSave(){
        if(this.petForm.valid && this.petId!==null){
            const payload:PetUpdate=this.petForm.value;
            this.petService.updatePet(this.petId, payload, localStorage.getItem('token')!).subscribe({
                next:()=>{
                    this.alerts.success('Éxito', 'Mascota actualizada correctamente');
                    this.router.navigate(['/listado-mascotas']);
                },
                error:()=>{
                    this.alerts.error('Error', 'No se pudo actualizar la mascota');
                    this.router.navigate(['/mascota/{}'.replace('{}',this.petId!.toString())]);
                }
                
            });
        }
            else{
                this.petForm.markAllAsTouched();
            }
    }
}
