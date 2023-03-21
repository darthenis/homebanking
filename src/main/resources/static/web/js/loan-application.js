const {createApp} = Vue;

createApp({
    data(){
        return{
            clientData: null,
            loansData : null,
            accountsData: null,
            themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
            item : "",
            isMobile: false,
            activeApply: false,
            data: {
                loanId: "",
                amount: null,
                payment: "1",
                accountNumber: ""
            },
            isLoading: false,
            messageAlert : "",
            activeModalConfirm : false
            }

    },
    created(){

        this.loadData();
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.clientData = JSON.parse(localStorage.getItem('clientData'));
        this.activeRandomWords(['car', 'house', 'travel', 'anything'])

    },
    methods:{
        loadData(){

            axios('/api/loans')
                .then(res => {

                    this.loansData = res.data;

                    let loanName = this.getParam("loan");

                    if(loanName){
            
                      this.data.loanId = this.loansData.find(loan => loan.name.toLowerCase() == loanName.toLowerCase()).id;
                      
                      this.activeApply = true
                    }
                    

            }).catch(err => console.log(err))

            
            axios('/api/clients/current/accounts')
                .then(res => {

                    this.accountsData = res.data;

            }).catch(err => console.log(err))


        },
        getParam(name){

          return new URLSearchParams(window.location.search).get(name);

        },
        selectLoanToApply(loanName){

         this.activeApply = true

         this.data.loanId = this.loansData.find(loan => loan.name === loanName).id;

        },
        getFormatCurrency(value){

          const formatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD' })

          return formatter.format(value);

        },
        getInterest(){

          

          if(!this.data.loanId) return;

          console.log(this.loansData)

          return this.loansData.find(loan => loan.id == this.data.loanId).interest;

        },
        getPaymentsById(){

          return this.loansData.filter(loan => loan.id === this.data.loanId)[0]?.payments;

        },
        activeRandomWords(array){

          this.item = array[0]

          let value = 1;

          setInterval(() => {

            if(value < array.length){

              this.item = array[value]

              value++

            } else {

              this.item = array[0]

              value = 1

            }

          }, 2000)


        },
        getAccountId(){

            return localStorage.getItem('selectedAccount');
  
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
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
        getMaxAmount(){

          return this.getFormatCurrency(this.loansData.filter(loan => loan.id === this.data.loanId)[0].maxAmount)

        },

        applyLoan(){

          this.isLoading = true;

          axios.post("/api/clients/current/loans", {...this.data})
              .then(res => {

                this.activeModalConfirm = false;

                this.isLoading = false;

                this.messageAlert = "Loan applicated sucessfully"

                setTimeout(() => this.messageAlert = "", 3000)

              }).catch(err => {

                this.activeModalConfirm = false;

                this.isLoading = false;

                this.messageAlert = err.response.data;
                
                setTimeout(() => this.messageAlert = "", 3000)

                console.log(err)

              })

        },
        logout(){

          axios.post('/api/logout').then(response => {
            
            localStorage.removeItem('clientData')
            
            window.location.href = '/web/index.html'
          
          })
    
        }
    }
}).mount('#app')