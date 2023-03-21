const {createApp} = Vue;

createApp({
    data(){
        return{
            clientData : null,
            themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
            activeBar: null,
            countries: null,
            isMobile: false,
            profile:{
                firstName : "",
                lastName : "",
                email : "",
                avatarUrl : "",
                country: "",
                address: "",
                tel : "",
            },
            error : {
              firstName : "",
              lastName: "",
              email : "",
              tel : ""
            },
            handlePassword : {
              actualPassword: "",
              newPassword : "",
              confirmNewPassword: ""


            },
            errorPassword: {

              actualPassword : "",
              newPassword : "",
              confirmNewPassword : ""

            },
            invalidFormEdit: true,
            avatarFileError : "",
            avatarFile : null,
            modalLoadAvatar : false,
            img : "",
            isLoading : false,
            isMobile: false,
            modalEditInfo : false,
            modalChangePassword : false,
            messageAlert : {
              message : "",
              isError: false
            }

        }
    },
    created(){

        this.loadData();
        this.clientData = JSON.parse(localStorage.getItem('clientData'));
        window.addEventListener("resize", this.resizeEvent);
        this.resizeEvent()
      },
      destroyed(){
    
        window.removeEventListener("resize", this.resizeEvent);
        this.resizeEvent()
      },
    methods : {
        loadData(){

            axios.get('/api/clients/current')
                    .then(res => {

                        this.clientData = {...res.data};

                        console.log(res.data)

                        for(let [key] of Object.entries(this.profile)){

                          this.profile[key] = res.data[key] || "";

                        }

                        this.img = res.data.avatarUrl || './assets/avatar.jpg'

                    })
            
            axios.get('https://restcountries.com/v3.1/all')
                    .then(res => {

                      this.countries = res.data.sort((a, b) => {

                            if ( a.name.common < b.name.common){
                              
                                return -1;

                            }

                            if (a.name.common > b.name.common) {

                                return 1;

                            }
                                return 0;


                      });

                    })

        },
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
        showModals(modal){

          if(modal){
            
            this[modal] = !this[modal]

            document.body.style.overflowY  = 'hidden';
          
          }
    
          else {
            
            this.modalEditInfo = false;
            this.modalLoadAvatar = false;
            this.modalChangePassword = false;
            document.body.style.overflowY  = 'scroll';
          }
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
      
            if(window.innerWidth <= 933 && window.innerWidth > 485){
      
              this.isTablet = true;
      
            } else {
      
              this.isTablet = false;
      
            }
      
        },
        toggleHandle(){

          this.themeDark = !this.themeDark;
    
          localStorage.setItem("themeBH", this.themeDark ? "dark" : "light");
    
        },
        getAnimationBar(barNum){

            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `animatedBar${barNum}Reverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `animatedBar${barNum}`};
      
            }
      
        },
        getAnimationNav(){
      
            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `navAnimateReverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `navAnimate`};
      
            }
      
          },
          resizeResetBarTogle(){
      
            this.activeBar = null;
      
          },
          getDateAndMonth(completeDate){

            let auxDate = completeDate.split('-').reverse();

            auxDate.pop();

            return auxDate.join("/");

          },
          logout(){

            axios.post('/api/logout').then(response => {
              
              localStorage.removeItem('clientData')
              
              window.location.href = '/web/index.html'
            
            })
      
          },

          readFile(){

            let reader = new FileReader();

            reader.onload = (event) => {
              
               this.img = event.target.result;

            };

            reader.readAsDataURL(this.avatarFile);

          },

          getFile(event){

            if(event.target.files[0].size > 250000){  //agregar alerta del tamaño
              this.avatarFileError = "The img is too big!";
             } else {
                this.avatarFile = event.target.files[0];
                this.readFile()
             }

          },
          getAccountId(){
  
            return localStorage.getItem('selectedAccount');
  
          },
          uploadBackFile(){
            let formData = new FormData();
            formData.append("avatarFile", this.avatarFile);
            this.isLoading = true;

            axios.post("/api/clients/current/avatar", formData, {
              headers: { 'Content-Type': 'multipart/form-data' }
            }).then((res) => {
              this.isLoading = false;
              this.invalidFormEdit = true;
              this.showModals('')
              this.clientData = null;
              location.reload(true)
            })
            .catch(err => {
              this.showModals('')
              this.isLoading = false;
              this.invalidFormEdit = true;
              console.log(err)
            
            })

          },
          changePass(){

            axios.post()

          },
          saveChanges(){

            this.isLoading = true;

            axios.patch('/api/clients/current', { firstName : this.profile.firstName, lastName : this.profile.lastName, 
                                                  email : this.profile.email, address : this.profile.address, 
                                                  country : this.profile.country, tel : this.profile.tel})
                        .then(res => {

                          this.showModals('')

                          this.loadData();

                          this.isLoading = false;

                          this.invalidFormEdit = true;

                          this.showMessage('Edited successfully', 3, false);

                        }).catch(err => {

                          console.log(err)

                          this.isLoading = false;

                          this.invalidFormEdit = true;

                          this.showModals('')

                          this.showMessage(err.response.data, 3, true);

                        })
          },
          showMessage(message, seconds, isError){

            this.messageAlert.message = message;

            this.messageAlert.isError = isError;

            setTimeout(() => this.messageAlert.message = "", seconds * 1000);

          },
          getAccountId(){

            return localStorage.getItem('selectedAccount');
  
          },
          check(){
            
            let valid = false;

            if(!this.profile.firstName){

              this.error.firstName = "*required"
              valid = true;
            } else {

              this.error.firstName = ""

            }
            
            if(!this.profile.lastName){

              this.error.lastName = "*required"
              valid = true;

            } else {

              this.error.lastName = ""

            }

            if(!this.profile.email){

              this.error.email = "*required"
              valid = true;

            } else {

              this.error.email = ""

            }

            this.invalidFormEdit = valid;

          },
          checkPassword(){

            let regex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,16}$/;

            if(!this.handlePassword.password){

              this.errorPassword.actualPassword = "*is required";
              this.invalidFormEdit = true;

            } else {

              this.errorPassword.actualPassword = "";

            }

            if(!regex.test(this.handlePassword.newPassword)){

              this.errorPassword.newPassword = "your password must has 6 to 16 valid characters," +
                                                "has at least a number, and a special character. "
              this.invalidFormEdit = true;
            } else {

              this.errorPassword.newPassword = ""

            }


            if(this.handlePassword.newPassword === this.handlePassword.confirmNewPassword){

                      this.handlePassword.confirmNewPassword = "your confirm new password must match with the new password"
                      this.invalidFormEdit = true;
                    } else {

                      this.errorPassword.confirmNewPassword = ""

                    }

          },
          changePassword(){

            this.checkPassword();

            if(Object.entries(this.errorPassword).some(errorPassword => errorPassword[1] != "")){

              this.isLoading = true;

              axios.post('/clients/current/changePassword', `password=${this.handlePassword.actualPassword}&newPassword=${this.handlePassword.newPassword}`)
                  .then(res => {

                    this.showModals('')

                    this.loadData();

                    this.isLoading = false;

                    this.invalidFormEdit = true;

                    this.showMessage('changed password successfully', 3, false);

                  }).catch(err => {


                    console.log(err)

                    this.isLoading = false;

                    this.invalidFormEdit = true;

                    this.showModals('')

                    this.showMessage(err.response.data, 3, true);


                  })

            }

          }
    }


}).mount('#app')