import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TableColumn {
  header: string;
  field: string;
}

@Component({
  selector: 'app-generic-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list.component.html',
})
export class GenericListComponent {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;

  // Evento para emitir la acción al padre (RankingComponent)
  @Output() actionClick = new EventEmitter<any>();

  onAction(item: any) {
    this.actionClick.emit(item);
  }
}