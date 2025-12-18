export enum TipoMascota {
    PERRO = 'PERRO',
    GATO = 'GATO',
    COBAYA = 'COBAYA',
    LORO = 'LORO',
    CONEJO = 'CONEJO',
    CABALLO = 'CABALLO',
    TORTUGA = 'TORTUGA',
}

export enum State {
    PERDIDO_PROPIO='PERDIDO_PROPIO',
    PERDIDO_AJENO='PERDIDO_AJENO',
    RECUPERADO='RECUPERADO',
    ADOPTADO='ADOPTADO',
}


export enum Size {
    PEQUENO='PEQUENO',
    MEDIANO='MEDIANO',
    GRANDE='GRANDE',
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
    state: State;
    type: TipoMascota;
    photoBase64: string;
}

export interface PetResponse{
    id:number,
    name:string,
    size: Size,
    description: string;
    color: string;
    race: string;
    weight: number;
    latitude: number;
    longitude: number;
    lostDate:Date,
    state:State,
    type:TipoMascota,
    creatorId:number,
    photosBase64: string[];
}

export interface PetFilter{
    name?:string,
    state?:State,
    type?:TipoMascota;
    size?:Size;
    color?:string;
    race?:string;
    weightMin?:number;
    weightMax?: number;
    initialLostDate?:Date;
    finalLostDate?:Date;
    userLatitude?:number;
    userLongitude?:number;
    maxDistanceKm?:number;
}

export interface PetUpdate{
        name: string,
        description:string,
        color: string,
        size:Size,
        race:string,
        weight: number,
        type:TipoMascota,
        state:State,
        latitude:number,
        longitude:number,
        photoBase64:string
}
