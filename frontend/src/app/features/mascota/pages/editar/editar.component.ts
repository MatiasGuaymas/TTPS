import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../mascota.service';
import { AlertService } from '../../../../core/services/alert.service';
import { PetUpdate, Size, State, TipoMascota } from '../../mascota.model';
import { MapComponent } from "../../../../shared/components/map/map";

@Component({
  selector: 'app-editar',
  imports: [ReactiveFormsModule, MapComponent],
  templateUrl: './editar.component.html',
  styleUrl: './editar.component.css',
})
export class PetEditComponent implements OnInit {
    private fb = inject(FormBuilder);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private petService = inject(MascotaService);
    private alerts = inject(AlertService);

    tiposMascota = Object.values(TipoMascota) as string[];
    estadosMascota = Object.values(State) as string[];
    tamanosMascota = Object.values(Size) as string[];

    petForm: FormGroup;
    petId: number | null = null;
    loading = signal(true);
    imagePreview = signal<string | ArrayBuffer | null>(null);
    imageBase64 = signal<string | undefined>(undefined);

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

                let photo=pet.photosBase64?.[0]||'';

                const displayPhoto = photo.startsWith('data:image')
                ? photo
                : `data:image/jpeg;base64,${photo}`;

                this.petForm.patchValue({
                    ...pet,
                    photoBase64: photo
                });
                this.imagePreview.set(photo ? displayPhoto : null);
                this.loading.set(false);
            },
            error: () => {
                this.alerts.error('Error', 'No se pudo encontrar a la mascota');
                this.router.navigate(['/listado-mascotas']);
            }
        });
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Validar tipo de archivo
            if (!file.type.startsWith('image/')) {
                this.alerts.error('Error', 'Solo se permiten archivos de imagen');
                return;
            }

            // Validar tamaño (máx 5MB)
            if (file.size > 5 * 1024 * 1024) {
                this.alerts.error('Error', 'La imagen no puede superar los 5MB');
                return;
            }

            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = e => {
                const base64String = (reader.result as string).split(',')[1];
                this.imageBase64.set(base64String);
                this.imagePreview.set(reader.result);
            };
            reader.onerror = (error) => {
                console.error('Error al convertir a Base64: ', error);
                this.imageBase64.set(undefined);
                this.imagePreview.set(null);
                this.alerts.error('Error', 'No se pudo procesar la imagen seleccionada');
            };
        }
    }

    /* onFileSelected(event: any) {
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
    }*/

    onLocationChanged(coords: { latitude: number; longitude: number }) {
        this.petForm.patchValue({
            latitude: coords.latitude,
            longitude: coords.longitude
        });
    }

    onSave(){
        if(this.petForm.valid && this.petId!==null){
            const payload:PetUpdate=this.petForm.value;
            payload.photoBase64=this.imageBase64()!;
            console.log(payload)
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

    goToPetDetail(petId: number) {
        this.router.navigate(['/mascota/{}'.replace('{}', petId.toString())]);
    }
}
