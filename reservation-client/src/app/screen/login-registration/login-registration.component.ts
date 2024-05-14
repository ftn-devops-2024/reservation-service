import { Component } from '@angular/core';
import {FormGroup, FormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {LoginInfo, User} from "../../model/user";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-login-registration',
  standalone: true,
  imports: [
    MatCardContent,
    MatProgressSpinner,
    FormsModule,
    CommonModule
  ],
  templateUrl: './login-registration.component.html',
  styleUrl: './login-registration.component.scss'
})
export class LoginRegistrationComponent {
  showLogin: boolean = true;
  user: User = new User();
  loginInfo: LoginInfo = new LoginInfo();
  loginData: any;
  form: FormGroup = new FormGroup({});
  isRegistered: boolean = false;
  reenterPass: string = '';

  registerClick() {
    this.showLogin = false;
  }

  loginUser(){}

  registerUser(){}

}
