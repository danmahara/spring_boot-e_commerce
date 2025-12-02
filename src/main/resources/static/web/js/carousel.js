// ============================================
// CAROUSEL.JS - Hero Carousel Controller
// ============================================

class Carousel {
  constructor(element) {
    this.carousel = element;
    this.slides = this.carousel.querySelectorAll('.carousel-slide');
    this.prevBtn = this.carousel.querySelector('.carousel-prev');
    this.nextBtn = this.carousel.querySelector('.carousel-next');
    this.dotsContainer = this.carousel.querySelector('.carousel-dots');
    
    this.currentIndex = 0;
    this.autoSlideInterval = null;
    this.autoSlideDelay = 5000; // 5 seconds
    
    this.init();
  }
  
  init() {
    // Create dots for each slide
    this.createDots();
    
    // Set up event listeners
    this.prevBtn.addEventListener('click', () => this.prevSlide());
    this.nextBtn.addEventListener('click', () => this.nextSlide());
    
    // Dot click events
    this.dots = this.carousel.querySelectorAll('.carousel-dot');
    this.dots.forEach((dot, index) => {
      dot.addEventListener('click', () => this.goToSlide(index));
    });
    
    // Pause on hover
    this.carousel.addEventListener('mouseenter', () => this.stopAutoSlide());
    this.carousel.addEventListener('mouseleave', () => this.startAutoSlide());
    
    // Touch events for mobile
    this.handleTouchEvents();
    
    // Show first slide
    this.showSlide(this.currentIndex);
    
    // Start auto-sliding
    this.startAutoSlide();
  }
  
  createDots() {
    this.slides.forEach((_, index) => {
      const dot = document.createElement('button');
      dot.classList.add('carousel-dot');
      dot.setAttribute('aria-label', `Go to slide ${index + 1}`);
      if (index === 0) dot.classList.add('active');
      this.dotsContainer.appendChild(dot);
    });
  }
  
  showSlide(index) {
    // Hide all slides
    this.slides.forEach((slide) => {
      slide.classList.remove('active');
    });
    
    // Remove active class from all dots
    this.dots.forEach((dot) => {
      dot.classList.remove('active');
    });
    
    // Show current slide
    this.slides[index].classList.add('active');
    this.dots[index].classList.add('active');
    
    this.currentIndex = index;
  }
  
  nextSlide() {
    let nextIndex = this.currentIndex + 1;
    if (nextIndex >= this.slides.length) {
      nextIndex = 0;
    }
    this.showSlide(nextIndex);
  }
  
  prevSlide() {
    let prevIndex = this.currentIndex - 1;
    if (prevIndex < 0) {
      prevIndex = this.slides.length - 1;
    }
    this.showSlide(prevIndex);
  }
  
  goToSlide(index) {
    this.showSlide(index);
    this.stopAutoSlide();
    this.startAutoSlide();
  }
  
  startAutoSlide() {
    this.stopAutoSlide(); // Clear any existing interval
    this.autoSlideInterval = setInterval(() => {
      this.nextSlide();
    }, this.autoSlideDelay);
  }
  
  stopAutoSlide() {
    if (this.autoSlideInterval) {
      clearInterval(this.autoSlideInterval);
      this.autoSlideInterval = null;
    }
  }
  
  handleTouchEvents() {
    let touchStartX = 0;
    let touchEndX = 0;
    
    this.carousel.addEventListener('touchstart', (e) => {
      touchStartX = e.changedTouches[0].screenX;
    });
    
    this.carousel.addEventListener('touchend', (e) => {
      touchEndX = e.changedTouches[0].screenX;
      this.handleSwipe(touchStartX, touchEndX);
    });
  }
  
  handleSwipe(startX, endX) {
    const swipeThreshold = 50;
    const diff = startX - endX;
    
    if (Math.abs(diff) > swipeThreshold) {
      if (diff > 0) {
        // Swipe left - next slide
        this.nextSlide();
      } else {
        // Swipe right - prev slide
        this.prevSlide();
      }
      this.stopAutoSlide();
      this.startAutoSlide();
    }
  }
}

// Initialize carousel when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  const carouselElement = document.querySelector('.hero-carousel');
  if (carouselElement) {
    new Carousel(carouselElement);
  }
  
  // Mobile menu toggle
  const mobileToggle = document.querySelector('.mobile-menu-toggle');
  const navMenu = document.querySelector('.nav-menu');
  
  if (mobileToggle && navMenu) {
    mobileToggle.addEventListener('click', () => {
      navMenu.classList.toggle('active');
      
      // Update icon
      const icon = mobileToggle.querySelector('i');
      if (icon) {
        if (navMenu.classList.contains('active')) {
          icon.classList.remove('fa-bars');
          icon.classList.add('fa-times');
        } else {
          icon.classList.remove('fa-times');
          icon.classList.add('fa-bars');
        }
      }
    });
    
    // Close menu when clicking on a link
    const navLinks = navMenu.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
      link.addEventListener('click', () => {
        navMenu.classList.remove('active');
        const icon = mobileToggle.querySelector('i');
        if (icon) {
          icon.classList.remove('fa-times');
          icon.classList.add('fa-bars');
        }
      });
    });
  }
  
  // Smooth scroll for anchor links
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
      const href = this.getAttribute('href');
      if (href !== '#') {
        e.preventDefault();
        const target = document.querySelector(href);
        if (target) {
          target.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
          });
        }
      }
    });
  });
  
  // Add to cart animation
  const addToCartBtns = document.querySelectorAll('.btn-add-cart');
  addToCartBtns.forEach(btn => {
    btn.addEventListener('click', function() {
      const originalText = this.textContent;
      this.textContent = '✓ Added!';
      this.style.background = 'var(--success-color)';
      
      setTimeout(() => {
        this.textContent = originalText;
        this.style.background = '';
      }, 2000);
      
      // Update cart count
      const cartCount = document.querySelector('.cart-count');
      if (cartCount) {
        const currentCount = parseInt(cartCount.textContent) || 0;
        cartCount.textContent = currentCount + 1;
      }
    });
  });
});
