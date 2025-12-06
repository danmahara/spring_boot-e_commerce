

// tinymce.init({
//     selector: 'textarea',
//     plugins: [
//         // Core editing features
//         'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
//         // Your account includes a free trial of TinyMCE premium features
//         // Try the most popular premium features until Dec 20, 2025:
//         'checklist', 'mediaembed', 'casechange', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'advtemplate', 'ai', 'uploadcare', 'mentions', 'tinycomments', 'tableofcontents', 'footnotes', 'mergetags', 'autocorrect', 'typography', 'inlinecss', 'markdown', 'importword', 'exportword', 'exportpdf'
//     ],
//     toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography uploadcare | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
//     tinycomments_mode: 'embedded',
//     tinycomments_author: 'Author name',
//     mergetags_list: [
//         { value: 'First.Name', title: 'First Name' },
//         { value: 'Email', title: 'Email' },
//     ],
//     ai_request: (request, respondWith) => respondWith.string(() => Promise.reject('See docs to implement AI Assistant')),
//     uploadcare_public_key: 'd6f74b4f5888475aa2c6',
// });


tinymce.init({
    selector: 'textarea',  // applies to your description field
    // plugins: [
    //     // Core editing features
    //     'anchor autolink charmap codesample emoticons link lists media searchreplace table visualblocks wordcount',
    //     // Premium / extra features
    //     'checklist mediaembed casechange formatpainter pageembed a11ychecker tinymcespellchecker permanentpen powerpaste advtable advcode advtemplate ai uploadcare mentions tinycomments tableofcontents footnotes mergetags autocorrect typography inlinecss markdown importword exportword exportpdf'
    // ],
    plugins: [
        // Core editing features
        'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
        // Your account includes a free trial of TinyMCE premium features
        // Try the most popular premium features until Dec 20, 2025:
        'checklist', 'mediaembed', 'casechange', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'advtemplate', 'ai', 'uploadcare', 'mentions', 'tinycomments', 'tableofcontents', 'footnotes', 'mergetags', 'autocorrect', 'typography', 'inlinecss', 'markdown', 'importword', 'exportword', 'exportpdf'
    ],
    toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography uploadcare | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',

    // Tiny comments setup
    tinycomments_mode: 'embedded',
    tinycomments_author: 'Author name',

    // Merge tags
    mergetags_list: [
        { value: 'First.Name', title: 'First Name' },
        { value: 'Email', title: 'Email' },
    ],

    // AI setup placeholder
    ai_request: (request, respondWith) => respondWith.string(() => Promise.reject('See docs to implement AI Assistant')),
    uploadcare_public_key: 'd6f74b4f5888475aa2c6',

    // ----------------------------
    // NEW: Sync content to <textarea> for AJAX form submission
    // ----------------------------
    setup: function (editor) {
        editor.on('change', function () {
            editor.save();  // always updates the <textarea> with editor content
        });
    },

    height: 400, // optional: make editor taller
    menubar: false,
    branding: false
});
