const { createApp } = Vue;

createApp({
    data(){
        return{
            isMobile : false,
            clientData : null,
            accountsData : null,
            themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
            activeBar: null,
            flipColourActive : null,
            activeDisappear : false,
            selectedTransferType : 3,
            selectButtons : true,
            activeModalConfirm: false,
            modalMessage : {
              message: "",
              isError: false
            },
            data:{
              myAccount : "Select your account",
              destinyAccount : "Select your account",
              amount : "0.00"
            },
        }
    },
    created(){
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.loadData()
        this.clientData = JSON.parse(localStorage.getItem('clientData'));
    },
    methods:{
        loadData(){

            axios('/api/clients/current/accounts')
                .then(res => {

                    this.accountsData = res.data;

                })

        },
        selectTransferType(num){

          this.activeFlipColourAnimation(num)
            
          setTimeout(() => this.selectedTransferType = num, 500)  

          setTimeout(() => this.selectButtons = false, 500)

        },
        activeFlipColourAnimation(num){

          this.flipColourActive = num;

          setTimeout(() => this.activeDisappear = true, 500)

        },
        activeMessageAlert(message, seconds, isError){

          this.modalMessage = {
            message : message,
            isError : isError
          }

          setTimeout(() => this.modalMessage.message = "", seconds * 1000);

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
        desactiveSelecte(){

                this.activeModalConfirm = false;

                //this.selectedTransferType = 3;

                //this.flipColourActive = null;

                //this.activeDisappear = false;

                //this.selectButtons = true;

        },
        transfer(){

          let amount = Number(this.data.amount.replace(/[^0-9.-]+/g,""));

          console.log(amount)

          axios.post(`/api/clients/current/transactions`, `origin=${this.data.myAccount}&destination=${this.data.destinyAccount}&amount=${amount}`,
          {headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(res => {

                this.activeMessageAlert("Transfer relized successfully", 2, false)

                this.desactiveSelecte()

                }).catch(err => {

                  this.desactiveSelecte();

                  console.log("err: ", err)

                  this.activeMessageAlert(err.response.data, 2, false)


                })

        },
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
        getAccountId(){
  
          return localStorage.getItem('selectedAccount');

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
        changeAmount(event){

          console.log(event.target.value)

          this.data.amount = "asd"

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
          formatNumber(n) {
            // format number 1000000 to 1,234,567
            return n.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
          },
          setCurrency(event){

            if(/[0-9]/.test(event.key)){

              if(this.data.amount.includes('0.00') && this.data.amount.length == 4){

                this.data.amount = "0.0"+event.key;

              } else if(this.data.amount.includes('0.0') && this.data.amount.length == 4){

                this.data.amount = "0." + this.data.amount[3] + event.key;

              } else if(this.data.amount[0] === '0' && this.data.amount.length == 4){

                this.data.amount = this.data.amount[2] + "." + this.data.amount[3] + event.key;

              } else {

                let test = this.data.amount;

                if(this.data.amount.includes(',')){

                  test = test.replace(/,/g, '')

                }

                test = test.split('.')

                let aux1 = test[0] + test[1][0];

                let aux2 = test[1].substring(1) + event.key;

                this.data.amount = parseInt(aux1).toLocaleString("en-US") + "." + aux2

              }

              
            } else if(event.key === "Backspace" && this.data.amount.length){

              let test = this.data.amount;

              if(this.data.amount.includes(',')){

                test = test.replace(/,/g, '')

              }

              let test1 = test.split('.')

              let aux1 = test1[0][test1[0].length - 1] + test1[1].slice(0, -1);

              let aux2 = test1[0].slice(0, -1) ;

              if(!aux2) aux2 = 0;

              else aux2 = parseInt(aux2).toLocaleString("en-US")

              this.data.amount = aux2 + "." + aux1;
              
            }

          },
          logout(){

            axios.post('/api/logout').then(response => {
              
              localStorage.removeItem('clientData')
              
              window.location.href = '/web/index.html'
            
            })
      
          },
    },
}).mount("#app")