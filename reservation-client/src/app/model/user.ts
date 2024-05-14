export class User {
  constructor(
    public name: string = '',
    public surname: string = '',
    public email: string = '',
    public password: string = '',
    public deleted: boolean = false
  ) {}
}

export class LoginInfo {
  constructor(
    public email: string = '',
    public password: string = '',
  ) {}
}
