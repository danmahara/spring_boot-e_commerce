# ShopHub - Complete eCommerce Landing Page

A beautiful, modern, and fully responsive eCommerce website with product listing and details pages.

## 📦 Files Included

- `index.html` - Main landing page with hero carousel, featured products, and footer
- `products.html` - Products listing page with filters, sorting, and pagination
- `product-details.html` - Detailed product page with image gallery, reviews, and specifications
- `core.css` - Complete theme system with CSS variables and reusable components
- `product-details.css` - Additional styles for product details page
- `carousel.js` - JavaScript for carousel functionality and interactions

## 🎨 Features

### Landing Page (index.html)
- ✅ Responsive hero carousel with auto-slide
- ✅ Featured products grid (8 products)
- ✅ Sticky navigation header
- ✅ Shopping cart counter
- ✅ Mobile-responsive design
- ✅ Footer with links and social media

### Products Page (products.html)
- ✅ Sidebar filters (categories, price, brands, ratings)
- ✅ Product grid view
- ✅ Sort options (price, rating, newest)
- ✅ Pagination
- ✅ Mobile filter drawer
- ✅ View toggle options

### Product Details Page (product-details.html)
- ✅ Image gallery with thumbnails
- ✅ Color and size selection
- ✅ Quantity selector
- ✅ Add to cart functionality
- ✅ Product tabs (description, specifications, reviews)
- ✅ Related products section
- ✅ Breadcrumb navigation
- ✅ Share buttons

## 🎨 Theme Customization

To change the entire website's color scheme, edit the CSS variables in `core.css` (lines 16-50):

```css
:root {
  /* Change these colors to update the entire theme */
  --primary-color: #0ea5e9;      /* Main brand color */
  --secondary-color: #8b5cf6;    /* Secondary accent */
  --accent-color: #f59e0b;       /* Highlights & badges */
  --background-color: #ffffff;   /* Page background */
  --text-color: #0f172a;         /* Main text */
}
```

### Example Theme Changes:

**Dark Theme:**
```css
--background-color: #0f172a;
--card-bg: #1e293b;
--text-color: #f1f5f9;
```

**Green Theme:**
```css
--primary-color: #10b981;
--secondary-color: #059669;
--accent-color: #fbbf24;
```

## 📁 File Structure

```
ecommerce-website/
├── index.html              # Landing page
├── products.html           # Products listing
├── product-details.html    # Product details
├── core.css               # Main stylesheet with theme system
├── product-details.css    # Product page specific styles
├── carousel.js            # JavaScript functionality
└── README.md              # This file
```

## 🚀 Getting Started

1. **Extract all files** to the same folder
2. **Open `index.html`** in your web browser
3. **Customize** the colors by editing CSS variables in `core.css`

## 📱 Responsive Design

The website is fully responsive and works on:
- 📱 Mobile phones (320px+)
- 📱 Tablets (768px+)
- 💻 Desktops (1024px+)
- 🖥️ Large screens (1440px+)

## 🎯 Key Components

### Buttons
- `.btn-primary` - Primary action button
- `.btn-secondary` - Secondary button
- `.btn-outline` - Outline style button
- `.btn-ghost` - Minimal button

### Cards
- `.product-card` - Product display card with hover effects
- `.card` - Generic card component

### Layout
- `.container` - Centered content container (max-width: 1280px)
- `.grid-2`, `.grid-3`, `.grid-4` - Responsive grid systems
- `.flex`, `.flex-center`, `.flex-between` - Flexbox utilities

### Spacing
- `.mt-lg`, `.mb-xl`, `.p-2xl`, etc. - Margin and padding utilities

## 🌟 Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## 📝 Customization Tips

1. **Add More Products**: Copy a product card and update image, title, price
2. **Change Fonts**: Update `--font-heading` and `--font-body` in core.css
3. **Adjust Spacing**: Modify spacing variables (`--space-sm`, `--space-md`, etc.)
4. **New Color Scheme**: Change the primary, secondary, and accent colors

## ⚡ Performance

- No jQuery or Bootstrap dependencies
- Pure CSS animations
- Optimized images from Unsplash
- Minimal JavaScript footprint

## 🎨 Design Credits

- Font: Outfit (headings), DM Sans (body text) from Google Fonts
- Icons: Font Awesome 6.4.0
- Images: Unsplash

## 📄 License

Free to use for personal and commercial projects.

---

Built with ❤️ for modern eCommerce

For support or questions, refer to the inline comments in the code files.
