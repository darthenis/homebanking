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
                password:  ""
            },
            isLoading : false,
            typePassword : "password",
            isSignInFormActived : true
        }
    },
    methods: {
        login(email, password){
            this.isLoading = true;
            axios.post('/api/login',`email=${email}&password=${password}`,
                        {headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(res => {

                        window.location.href = "/web/accounts.html"

                    })
                    .catch(err => {
                        console.log(err)
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
                    })
        },
        register(){

            this.isLoading = true;
            axios.post('/api/clients',`firstName=${this.signUp.firstName}&lastName=${this.signUp.lastName}&email=${this.signUp.email}&password=${this.signUp.password}`,
                        {headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(res => {

                        this.login(this.signUp.email, this.signUp.password)

                    })
                    .catch(err => {
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

        }
    }

}).mount('#app')