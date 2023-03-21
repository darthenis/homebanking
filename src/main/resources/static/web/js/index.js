const {createApp} = Vue;

createApp({
    data(){
        return{
            signIn:{
                email : "",
                password:  ""
            },
            signUp : {
                firstName: "",
                lastName: "",
                email : "",
                confirmEmail : "",
                password:  "",
                confirmPassword: "",
                errors: {
                    firstName: "",
                    lastName: "",
                    email: "",
                    confirmEmail: "",
                    password: "",
                    confirmPassword: ""
                }
            },
            secondsCounter : 5,
            isLoading : false,
            typePassword : "password",
            isSignInFormActived : true,
            firstRender : true,
            confirmEmail : false,
            activingEmail : false,
            emailActived: null,
            activeResendEmailForm: false,
            emailToResend : "",
            resendingEmail : null,
            emailAlreadyActive: false,
            activeLogin : false,
            isMobileSmall: false,
            isMobile : false,
            activeBar: null
        }
    },
    created(){

        const token = this.getParamFromUrl("token");

        if(token){

            this.confirmEmail = true;

            this.activeEmail(token);

        }

        window.addEventListener("resize", this.setIsMobile);

        this.setIsMobile();

    },
    methods: {
        resendEmailHandler(){

            this.resendingEmail = true;

            this.isLoading = true;

            this.emailAlreadyActive = false;

            axios.post("/api/clients/resend", `email=${this.emailToResend}`, {headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(res => {
    
                        this.activeResendEmailForm = false;
                        this.resendingEmail = false;
                        this.isLoading = false;

                    }).catch(err => {

                        this.resendingEmail = false;
                        this.activeResendEmailForm = false;
                        this.isLoading = false;

                        if(err.response.data == "User already active"){

                            this.emailAlreadyActive = true;

                            Toastify({
                                text: "The account is already active",
                                className: "info",
                                position: "center",
                                duration: 4000,
                                style: {
                                background: "#9b1d1d",
                                }
                            }).showToast();

                        }

                        

                    })
    
        },
        setIsMobile(){

            if(window.innerWidth <= 646){
      
                this.isMobile = true;
        
              } else {
        
                this.isMobile = false;
        
              }
      
            if(window.innerWidth <= 485){
      
              this.isMobileSmall = true;
      
            } else {
      
              this.isMobileSmall = false;
      
            }
      
        },
        getAnimationBar(barNum){

            if(!this.activeBar && this.activeBar !== null){
    
            return {'animation-name': `animatedBar${barNum}Reverse`};
    
            } else if(this.activeBar){
    
            return {'animation-name': `animatedBar${barNum}`};
    
            }
  
        },
        loadIndex(){

            this.activeLogin = false;
            this.isSignInFormActived = true;
        },
        activeRegister(){

            this.activeLogin = true;
            this.isSignInFormActived = false;

        },
        activeResendForm(){

            this.activeResendEmailForm = true;

        },

        secondsCounterHandler(){

            setInterval(() => this.secondsCounter--, 1000); 

        },

        getParamFromUrl(param){

            return new URLSearchParams(window.location.search).get(param);
        
        },
        backToLogin(){

            window.location =  "/web/index.html"

        },
        activeEmail(token){

                this.activingEmail = true;

                setTimeout(() =>   axios.post('/api/clients/active', 'token='+token, {headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(res => {
                    this.activingEmail = false;
                    this.emailActived = true;
                    this.secondsCounterHandler();
                    setTimeout(() => window.location.href = "/web/accounts.html",5000)
                })
                .catch(err => {
                    this.activingEmail = false;
                    this.emailActived = false;

                }), 3000)
        
        },
        login(email, password){
            this.isLoading = true;
            axios.post('/api/login',`email=${email}&password=${password}`,
                        {headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(res => {

                        this.isLoading = false;

                        location.reload();

                    })
                    .catch(err => {

                        if(err.response.data.status === 403){

                            Toastify({
                                text: "Email not confirmed",
                                className: "info",
                                position: "center",
                                duration: 4000,
                                style: {
                                background: "#9b1d1d",
                                }
                            }).showToast();

                        } else {

                            this.isLoading = false;
                            this.clearForm();
                            Toastify({
                                text: "Invalid credentials",
                                className: "info",
                                position: "center",
                                duration: 4000,
                                style: {
                                background: "#9b1d1d",
                                }
                            }).showToast();
                        }

                    })
        },
        register(){
            
            this.handleValidator(true);

            if(!Object.values(this.signUp.errors).some(error => error !== "")){

                this.isLoading = true;

                axios.post('/api/clients',{firstName : this.signUp.firstName, lastName : this.signUp.lastName, email: this.signUp.email, password : this.signUp.password})
                        .then(res => {

                            this.confirmEmail = true;
                            this.isLoading = false;

                        })
                        .catch(err => {
                            console.log(err)
                            this.isLoading = false;
                            this.clearForm();
                            Toastify({
                                text: err.response.data,
                                className: "info",
                                position: "center",
                                duration: 3000,
                                style: {
                                background: "#9b1d1d",
                                }
                            }).showToast();
                        })

            }

        },
        clearForm(){
            this.email = "",
            this.password = ""

        },
        handleViewPassword(){

            if(this.typePassword === "password"){

                this.typePassword = "text"

            } else {

                this.typePassword = "password"

            }

        },
        handleFormActive(){

            this.isSignInFormActived = !this.isSignInFormActived;

            this.firstRender = false;

        },
        handleValidator(isSending){

            console.log(this.signUp.email)

            for(let [key] of Object.entries(this.signUp.errors)){

                if (isSending && !this.signUp[key]) this.signUp.errors[key] = "*required field";

                else if(this.signUp[key]) this.signUp.errors[key] = "";

                if(this.signUp[key] || key === "confirmEmail" && this.signUp.email || key === "confirmPassword" && this.signUp.password){

                    if(!/^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$/g.test(this.signUp[key]) && 
                        (   key === "firstName" || 
                            key === "lastName" )) this.signUp.errors[key] = "*only letters";
    
                    else if(key === "email" && 
                    !/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(this.signUp[key])) {
                        
                        this.signUp.errors.email = "*format email valid";
                    
                    } else if(key === "confirmEmail" && this.signUp[key] !== this.signUp.email){

                        this.signUp.errors.confirmEmail = "*the emails must match";

                    }else if(key === "password" && !/^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,16}$/.test(this.signUp[key])){

                        this.signUp.errors.password =   "the password must 6 to 16 valid characters," +
                                                        "has at least a number, and a special character. "

                    } else if(key === "confirmPassword" && this.signUp[key] !== this.signUp.password){

                        this.signUp.errors.confirmPassword = "*the passwords must match";

                    }

                } else if(this.signUp.errors[key] !== "*required field" && (key !== "confirmEmail" || key !== "confirmPassword")){

                    this.signUp.errors[key] = "";

                }

            }

        }
    },
    computed: {

        validating(){

            this.handleValidator(false);

        }

    }

}).mount('#app')