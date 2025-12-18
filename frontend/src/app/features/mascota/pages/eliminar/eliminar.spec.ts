import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Eliminar } from './eliminar';

describe('Eliminar', () => {
  let component: Eliminar;
  let fixture: ComponentFixture<Eliminar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Eliminar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Eliminar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
