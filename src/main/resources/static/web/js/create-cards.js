const {createApp} = Vue;

createApp({
    data(){
        return{
            clientData: null,
            themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
            cardType: "",
            cardColor: "",
            accountNumber: "",
            jsonLoad: './assets/card.json',
            optionsColor : [],
            optionsType : [],
            isLoading : false,
            isMobile: false,
            messageAlert : ""
        }
    },
    created(){

        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.loadData()
        this.getParamsFromUrl();
    },
    methods : {
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
        getDate(plusYear){

          let date = new Date();

          if(plusYear) return (date.getMonth() + 1) + "/" + (parseInt(date.getFullYear().toString().slice(2)) + 5); 

          return  (date.getMonth() + 1) + "/" + date.getFullYear().toString().slice(2);

        },
        getParamsFromUrl(){

          let typeParam = new URLSearchParams(window.location.search).get("type");

          let colorParam = new URLSearchParams(window.location.search).get("color");

          this.cardType = typeParam == "All" ? "" : typeParam;

          console.log(colorParam)

          this.cardColor = colorParam == "All" ? "" : colorParam;
      
        },
        loadData(){

            axios.get('http://localhost:8080/api/clients/current')
                  .then(res => {

                      this.clientData = res.data;

                      this.filterCards = [...res.data.cards];

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
        getOptionsTypeAvailable(){

          let options = [];

          let debit = this.clientData.cards.filter(card => card.cardType === "DEBIT");

          let credit = this.clientData.cards.filter(card => card.cardType === "CREDIT");

          if(debit.length < 3) options.push("Debit");

          if(credit.length < 3) options.push("Credit");

          this.optionsType = options;

        },
        getAccountId(){

          return localStorage.getItem('selectedAccount');

        },
        getOptionsColorAvailable(){

          let options = [];

          let cards = this.clientData.cards.filter(card => card.cardType === this.cardType.toUpperCase());

          if(!cards.some(card => card.cardColor === "SILVER")) options.push("Silver")

          if(!cards.some(card => card.cardColor === "GOLD")) options.push("Gold")

          if(!cards.some(card => card.cardColor === "TITANIUM")) options.push("Titanium")

          this.optionsColor = options;

          this.cardColor = ""

        },
        logout(){

            axios.post('/api/logout').then(response => window.location.href = '/web/index.html')
      
        },
        createCard(){

          this.isLoading = true;

          axios.post(`/api/clients/current/cards?cardType=${this.cardType.toUpperCase()}&cardColor=${this.cardColor.toUpperCase()}${(this.accountNumber && this.cardType == 'Debit') ? '&accountNumber=' + this.accountNumber : ''}`,
          {headers:{'content-type':'application/x-www-form-urlencoded'}})
          .then(res => {

              window.location.href = "/web/cards.html";

          }).catch(err => {

            this.isLoading = false;
            
            if(err.response.status === 400) this.activeMessageAlert("Select type and color, please", 3)

            else this.activeMessageAlert(err.response.data, 3)

          })

        },
        activeMessageAlert(message, seconds){
          
          this.messageAlert = message;

          setTimeout(() => this.messageAlert = "", seconds * 1000)
        }

    },
    computed:{
      reload(){
        if(!this.clientData) return; 
        this.getOptionsColorAvailable()
        this.getOptionsTypeAvailable()
      }
    }

}).mount("#app")