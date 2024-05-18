import {RouterModule, Routes} from '@angular/router';
import {LoginRegistrationComponent} from "./screen/login-registration/login-registration.component";
import {NgModule} from "@angular/core";

export const routes: Routes = [
  { path: '', component: LoginRegistrationComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
