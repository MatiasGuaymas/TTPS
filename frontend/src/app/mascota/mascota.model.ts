export enum TipoMascota {
  PERRO = 'PERRO',
  GATO = 'GATO',
  COBAYA = 'COBAYA',
  LORO = 'LORO',
  CONEJO = 'CONEJO',
  CABALLO = 'CABALLO',
  TORTUGA = 'TORTUGA',
}

export interface PetCreate {
  name: string;
  size: string; 
  description: string;
  color: string; 
  race: string;
  weight: number; 
  latitude: number; 
  longitude: number;
  type: TipoMascota; 
  photoBase64: string;
}