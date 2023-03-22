const { createApp } = Vue;


createApp({
    data(){
        return {
            clientData : null,
            accountsDebit: [],
            filterCards : [],
            themeDark: true,
            activeBar: null,
            cardType : "All",
            cardColor: "All",
            numberAccount: "All",
            isMobile: false,
            isLoading: false,
            selectedCard : null,
            activeModalConfirm: false,
            modalMessage : {
              message :"",
              isError : false
            }


        }

    },
    created(){
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.loadData();
    },
    methods: {
        loadData(){

            axios.get('/api/clients/current')
                  .then(res => {

                      this.clientData = res.data;

                      this.filterCards = [...res.data.cards];

                      this.accountsDebit = this.getAccountsDebit();

                  })

        },
        getAccountId(){

          return localStorage.getItem('selectedAccount');

        },
        filteringCards(){

          if(this.cardType !== "All"){

            this.numberAccount = "All"

            this.filterCards = this.clientData.cards.filter(card => card.cardType.toLowerCase() === this.cardType.toLowerCase());
          
          } else {

            this.filterCards = [...this.clientData.cards]

          }

          if(this.cardColor !== "All"){

            this.filterCards = this.filterCards.filter(card => card.cardColor.toLowerCase() === this.cardColor.toLowerCase());

          }

          if(this.numberAccount !== "All"){

            this.filterCards = this.filterCards.filter(card => card.accountNumber == this.numberAccount);

          }

        },
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
    selectCardToDelete(card){

      this.selectedCard = card;

      this.activeModalConfirm = true;

    },
    checkDate(date){

      return new Date(date) <=   new Date()

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
      getAccountsDebit(){

        return new Set(this.clientData.cards.filter(card => card.cardType == "DEBIT").map(card => card.accountNumber));

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

            auxDate.shift();

            auxDate[1] = auxDate[1].slice(2)

            return auxDate.join("/");

          },
          logout(){

            axios.post('/api/logout').then(response => {
              
              localStorage.removeItem('clientData')
              
              window.location.href = '/web/index.html'
            
            })
      
          },
          createCard(){

            window.location.href = `/web/create-cards.html?type=${this.cardType}&color=${this.cardColor}`;
          },
          getStringSelectCards(plural){

            let card = plural ? " card" : " cards";

            if(this.cardType == "All" && this.cardColor == "All") return !plural ? "All your cards" : " card" ;

            if(this.cardType == "All" && this.cardColor != "All") return `${!plural ? "All your " : ""} ${this.cardColor + card}`;

            if(this.cardType != "All" && this.cardColor == "All") return `${!plural ? "All your " : ""} ${this.cardType + card}`;

            if(this.cardType != "All" && this.cardColor != "All") return `${!plural ? "Your " : ""} ${this.cardType}'s ${this.cardColor + " card"}`;

          },
          showMessage(message, seconds, isError){

            this.modalMessage.message = message;

            this.modalMessage.isError = isError;

            setTimeout(() => this.modalMessage.message = "", seconds * 1000);

          },
          deleteCard(){

            this.isLoading = true;

            axios.delete('/api/clients/current/cards/'+this.selectedCard.id)
              .then(res => {
                console.log(res)
                this.isLoading = false;
                this.activeModalConfirm = false;
                this.showMessage("Card deleted successfully", 3, false)
                this.loadData();


              }).catch(err => {
                console.log(err)
                this.isLoading = false;
                this.activeModalConfirm = false;
                this.showMessage("Error, try later", 3, true)

              })

          }

    },
    computed : {
      filtering(){
        this.getStringSelectCards();
        if(!this.clientData) return;
        this.filteringCards();
        
      }
    }
}).mount('#app')