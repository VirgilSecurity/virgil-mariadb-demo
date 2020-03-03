import React from 'react';
import ReactDOM from 'react-dom';
import './style.css'
import App from './App';
import * as serviceWorker from './serviceWorker';

if (document) document.querySelector('html')!.style.backgroundColor = '#eceff1';

ReactDOM.render(<App />, document.getElementById('root'));

serviceWorker.unregister();
