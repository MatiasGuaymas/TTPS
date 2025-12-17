export enum TipoMascota {
    PERRO = 'PERRO',
    GATO = 'GATO',
    COBAYA = 'COBAYA',
    LORO = 'LORO',
    CONEJO = 'CONEJO',
    CABALLO = 'CABALLO',
    TORTUGA = 'TORTUGA',
}

export enum TamanoMascota {
    PEQUENO = 'PEQUENO',
    MEDIANO = 'MEDIANO',
    GRANDE = 'GRANDE'
}

export enum EstadoMascota {
    PERDIDO_PROPIO = 'PERDIDO_PROPIO',
    PERDIDO_AJENO = 'PERDIDO_AJENO',
    RECUPERADO = 'RECUPERADO',
    ADOPTADO = 'ADOPTADO'
}

export interface Pet {
    id: number;
    name: string;
    size: TamanoMascota;
    description: string;
    color: string;
    race: string;
    weight: number;
    latitude: number;
    longitude: number;
    lostDate: string;
    state: EstadoMascota;
    type: TipoMascota;
    creatorId: number;
    photosBase64: string[];
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

