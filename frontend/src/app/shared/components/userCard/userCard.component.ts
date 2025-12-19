import { Component, Input, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { UserProfile } from "../../../core/models/user.models";

@Component({
    selector: 'user-card',
    standalone: true,
    imports: [CommonModule],
    templateUrl: 'userCard.component.html',
})
export class UserCardComponent {
    @Input() user!: UserProfile;
    @Input() position!: number;

    private router = inject(Router);

    get initials(): string {
        if (!this.user) return '';
        
        const first = this.user.name ? this.user.name.charAt(0).toUpperCase() : '';
        const last = this.user.lastName ? this.user.lastName.charAt(0).toUpperCase() : '';
        
        return `${first}${last}`;
    }

    verPerfil() {
        if (this.user && this.user.id) {
            this.router.navigate(['/user', this.user.id]);
        }
    }
}