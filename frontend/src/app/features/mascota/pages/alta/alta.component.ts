import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef, ChangeDetectorRef } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { PetCreate, Size, State, TipoMascota } from "../../mascota.model";
import { MascotaService } from "../../mascota.service";
import { AlertService } from '../../../../core/services/alert.service';
import { MapComponent } from "../../../../shared/components/map/map";

@Component({
    selector: "app-alta-mascota",
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, MapComponent],
    templateUrl: "./alta.component.html",
    styleUrls: ['./alta.component.css']
})
export class AltaMascota implements OnInit {

    formMascota!: FormGroup;

    tiposMascota = Object.values(TipoMascota);

    tamanosMascota = Object.values(Size);

    estados = Object.values(State);

    imagenPreVisualizacion: string | ArrayBuffer | null = null;

    imagenBase64: string | null = null;


    constructor(
        private fb: FormBuilder,
        private mascotaService: MascotaService,
        private alert: AlertService,
        private cdRef: ChangeDetectorRef
    ) {}



    ngOnInit(): void {
        this.formMascota = this.fb.group({
            name: ['', Validators.required],
            size: [Size.MEDIANO, Validators.required],
            type: [TipoMascota.PERRO, Validators.required],
            color: ['', Validators.required],
            race: ['', Validators.required],
            weight: [null, [Validators.required, Validators.min(0.1)]],
            description: ['', [Validators.required, Validators.maxLength(500)]],


            latitude: [ null, [Validators.required, Validators.min(-90), Validators.max(90)]],
            longitude: [ null, [Validators.required, Validators.min(-180), Validators.max(180)]],
            ownerName: [''],
            fechaDesaparicion: [''],
            estado: [State.PERDIDO_PROPIO],
            photoFile: [null, Validators.required]

        });
    }

    onCoordinatesChange(coords: { latitude: number, longitude: number }): void {
        this.formMascota.get('latitude')?.setValue(coords.latitude);
        this.formMascota.get('longitude')?.setValue(coords.longitude);
        this.formMascota.get('latitude')?.markAsTouched();
        this.formMascota.get('longitude')?.markAsTouched();
    }

    seleccionarEstado(estado: String): void{
        this.formMascota.get('estado')?.setValue(estado);
    }

    onFileSelected(event: Event): void{
        const input = event.target as HTMLInputElement;

        if (input.files && input.files.length > 0) {
            const file = input.files[0];

            const reader = new FileReader();
            reader.onload = e => {
                this.imagenPreVisualizacion = reader.result;
                this.cdRef.detectChanges();
            };
            reader.readAsDataURL(file);

            this.convertToBase64(file);
        } else {
            this.imagenPreVisualizacion = null;
            this.imagenBase64 = null;
        }
    }

    private convertToBase64(file: File): void {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            const base64String = (reader.result as string).split(',')[1];
            this.imagenBase64 = base64String;
        }
        reader.onerror = (error) => {
            console.error('Error al convertir a Base64: ', error);
            this.imagenBase64 = null;
        };
    }


    onSubmit(): void {
        if (this.formMascota.valid && this.imagenBase64) {
            const formValue = this.formMascota.value;

            const petCreateDto: PetCreate = {
                name: formValue.name,
                size: formValue.size,
                description: formValue.description,
                color: formValue.color,
                race: formValue.race,
                weight: formValue.weight,
                latitude: formValue.latitude,
                longitude: formValue.longitude,
                state: formValue.estado,
                type: formValue.type,
                photoBase64: this.imagenBase64!,
            };


            this.mascotaService.crearMascota(petCreateDto, localStorage.getItem("token") ?? "").subscribe({
                next: (response) => {
                    this.alert.success('Éxito', 'Mascota registrada con éxito!');
                    this.formMascota.reset();
                    this.imagenPreVisualizacion = null;
                    this.imagenBase64 = null;
                },
                error: (error) => {
                    console.error('Error al registrar mascota:', error);
                    this.alert.error('Error', 'Error al registrar mascota.');
                }
            });
        } else {
            this.alert.error('Campos incompletos', 'Por favor, completa todos los campos y selecciona una foto.');
        }
    }
}
