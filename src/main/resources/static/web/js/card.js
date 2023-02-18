const { createApp } = Vue;


createApp({
    data(){
        return {
            clientData : null,
            filterCards : [],
            themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
            cardType : "Debit",
            cardColor: "Silver",
            activeBar: null


        }

    },
    created(){
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.loadData();


    },
    methods: {
        loadData(){

            axios.get('http://localhost:8080/api/clients/1')
                  .then(res => {

                      this.clientData = res.data;

                      this.filterCards = this.firstFilter(res.data.cards);

                  })

        },
        firstFilter(cards){

          let filterByType = cards.filter(card => card.cardType === "DEBIT");

          return filterByType.filter(card => card.cardColor === "SILVER");

        },
        filteringCards(){

            console.log(this.clientData.cards[0].cardType.toLowerCase())

            let filterByType = this.clientData.cards.filter(card => card.cardType.toLowerCase() === this.cardType.toLowerCase());

            let filterByColor = filterByType.filter(card => card.cardColor.toLowerCase() === this.cardColor.toLowerCase());

            this.filterCards = filterByColor;

            console.log(filterByColor)

        },
        resizeEvent(){
            this.resizeResetBarTogle();
            this.setIsMobile();
        
        },
        setIsMobile(){

            if(window.screen.width <= 646){
      
              this.isMobile = true;
      
            } else {
      
              this.isMobile = false;
      
            }
      
            if(window.screen.width <= 485){
      
              this.isMobileSmall = true;
      
            } else {
      
              this.isMobileSmall = false;
      
            }
      
            if(window.screen.width <= 933 && window.screen.width > 485){
      
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

            axios.post('/api/logout').then(response => window.location.href = '/web/index.html')
      
          },

    },
    computed : {
      filtering(){
        if(!this.clientData) return;
        this.filteringCards();
      }
    }
}).mount('#app')