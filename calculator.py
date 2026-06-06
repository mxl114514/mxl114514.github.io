import tkinter as tk
from tkinter import font
import math

class Calculator:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title('Calc v0.0.1')
        self.root.geometry('360x520')
        self.root.resizable(False, False)
        self.root.configure(bg='#1c1c1e')
        self.expression = ''
        self.result_var = tk.StringVar(value='0')
        self.expr_var = tk.StringVar(value='')
        self._build_ui()
        self.root.bind('<Key>', self._key_handler)

    def _build_ui(self):
        df = tk.Frame(self.root, bg='#1c1c1e')
        df.pack(fill='x', padx=16, pady=(20,10))
        tk.Label(df, textvariable=self.expr_var, font=('Segoe UI',13),
                fg='#8e8e93', bg='#1c1c1e', anchor='e').pack(fill='x')
        tk.Label(df, textvariable=self.result_var, font=('Segoe UI',36,'bold'),
                fg='white', bg='#1c1c1e', anchor='e').pack(fill='x')

        bf = tk.Frame(self.root, bg='#1c1c1e')
        bf.pack(fill='both', expand=True, padx=12, pady=(0,16))
        buttons = [
            ['C','+/-','%','/'],
            ['7','8','9','*'],
            ['4','5','6','-'],
            ['1','2','3','+'],
            ['0','.','DEL','=']
        ]
        for r, row in enumerate(buttons):
            bf.rowconfigure(r, weight=1)
            for c, text in enumerate(row):
                bf.columnconfigure(c, weight=1)
                if text in '0123456789.':
                    bg, fg = '#505050', '#d4d4d4'
                elif text in '/*-+':
                    bg, fg = '#ff9f0a', 'white'
                elif text == '=':
                    bg, fg = '#ff9f0a', 'white'
                else:
                    bg, fg = '#3a3a3c', 'white'
                btn = tk.Button(bf, text=text, font=('Segoe UI',18,'bold'),
                               fg=fg, bg=bg, bd=0, activebackground='#636366',
                               command=lambda t=text: self._click(t))
                btn.grid(row=r, column=c, padx=3, pady=3, sticky='nsew')

    def _click(self, char):
        if char == 'C':
            self.expression = ''
            self.result_var.set('0')
            self.expr_var.set('')
        elif char == 'DEL':
            self.expression = self.expression[:-1]
            self._update()
        elif char == '+/-':
            if self.expression:
                self.expression = self.expression[1:] if self.expression.startswith('-') else '-'+self.expression
            self._update()
        elif char == '%':
            self.expression += '/100'
            self._calc()
        elif char == '=':
            self._calc()
        else:
            self.expression += char
            self._update()

    def _calc(self):
        try:
            self.expr_var.set(self.expression + ' =')
            ns = {'math':math,'pi':math.pi,'e':math.e,'sqrt':math.sqrt,'sin':math.sin,'cos':math.cos,'tan':math.tan,'log':math.log,'abs':abs,'round':round}
            result = eval(self.expression, {'__builtins__':{}}, ns)
            if isinstance(result, float):
                result = round(result, 10)
                if result == int(result):
                    result = int(result)
            self.result_var.set(str(result))
            self.expression = str(result)
        except:
            self.result_var.set('Error')
            self.expression = ''

    def _update(self):
        if not self.expression:
            self.result_var.set('0')
            self.expr_var.set('')
        else:
            self.expr_var.set(self.expression)
            self.result_var.set('')

    def _key_handler(self, event):
        km = {'0':'0','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','.':'.','+':'+','-':'-','*':'*','/':'/','Return':'=','BackSpace':'DEL','c':'C','C':'C','Escape':'C','percent':'%'}
        ch = km.get(event.keysym, km.get(event.char))
        if ch:
            self._click(ch)

    def run(self):
        self.root.mainloop()

if __name__ == '__main__':
    Calculator().run()
