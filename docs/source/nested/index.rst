Nested index page


..
   The toctree captions here seem to get lost at least in the sidebar
   and instead a, b, c, d are directly below the Nested caption.  You
   can fix the structure by adding headings to get section
   titles. However, I don’t understand why this is necessary since for
   the root index.rst, we don’t have those and the captions are
   preserved correctly.

.. toctree::
   :caption: nested 1

   a
   b

.. toctree::
   :caption: nested 2

   c
   d
