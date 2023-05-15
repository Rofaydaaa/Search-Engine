import React, { useState } from 'react';
import { Container } from 'react-bootstrap';
import SearchEngine from './Components/SearchEngine';
import {BrowserRouter,Router, Route, Routes } from 'react-router-dom';
import './App.css';
import ResultPage from './Components/ResultPage';

function App() {
    const [searchText, setSearchText] = useState('');
    return (
        <BrowserRouter>
            <Container>
                    <div className={"App-container"}>
                        <Routes>
                            <Route path="/" exact element={<SearchEngine handleSearch={setSearchText}/>}/>
                            <Route path="/Results" exact element={<ResultPage handleSearch={setSearchText} searchText={searchText}/>}/>
                        </Routes>
                    </div>
            </Container>
        </BrowserRouter>
    );
}

export default App;