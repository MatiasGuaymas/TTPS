import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Editar } from './editar';

describe('Editar', () => {
  let component: Editar;
  let fixture: ComponentFixture<Editar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Editar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Editar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
